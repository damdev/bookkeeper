package com.github.damdev.bookkeeper

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import com.github.damdev.bookkeeper.algebras.{ClientAlg, FileImportAlg, FileProcessorAlg, PaymentAlg}
import com.github.damdev.bookkeeper.algebras.impl._
import com.github.damdev.bookkeeper.config.ConfigFactory
import com.github.damdev.bookkeeper.db.impl.PaymentDBImpl
import com.github.damdev.bookkeeper.db.{DBTransactor, PaymentDBAlg}
import doobie._
import fs2.Stream
import org.http4s.client.blaze.{BlazeClientBuilder, BlazeClientConfig}
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.slf4j.LoggerFactory
import cats.syntax._

import scala.concurrent.ExecutionContext.global

object BookkeeperServer {
  import scala.concurrent.duration._
  private val logger = LoggerFactory.getLogger(this.getClass)

  def stream[F[_]: ConcurrentEffect](cronAlg: CronAlg[F])(implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      config <- Stream.fromEither[F](ConfigFactory.load)
      clientAlg: ClientAlg[F] = ClientImpl[F](client, config.bookkeeper.clientApi)
      fileImportAlg: FileImportAlg[F] = FileImportImpl[F](client, config.bookkeeper.fileImport)
      transactor = DBTransactor.build[F](config.bookkeeper.db)
      paymentDBAlg: PaymentDBAlg[ConnectionIO] = PaymentDBImpl()
      paymentAlg: PaymentAlg[F] = PaymentImpl(paymentDBAlg, transactor)

      _ <- Stream.eval(paymentAlg.initDB)

      fileProcessorAlg: FileProcessorAlg[F] = FileProcessorImpl[F](paymentAlg)

      _ <- cronAlg.cron(config.bookkeeper.fileImport.interval)(
        cronAlg.delay(logger.info("Start file import process"))
          *> fileProcessorAlg.processAndSave(fileImportAlg.importFile()).attempt.map(
            _.left.foreach(t => logger.error("Error importing file", t))
          ) <*
          cronAlg.delay(logger.info("End file import process")))

      httpApp = (
        BookkeeperRoutes.clientInfoRoutes[F](clientAlg) <+>
        BookkeeperRoutes.paymentRoutes[F](paymentAlg) <+>
        BookkeeperRoutes.transactionsRoutes[F](paymentAlg) <+>
        BookkeeperRoutes.fileImportRoutes[F](fileImportAlg, fileProcessorAlg)
      ).orNotFound

      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}