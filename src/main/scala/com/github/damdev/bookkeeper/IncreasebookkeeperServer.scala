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

import scala.concurrent.ExecutionContext.global

object IncreasebookkeeperServer {

  def stream[F[_]: ConcurrentEffect](implicit T: Timer[F], C: ContextShift[F]): Stream[F, Nothing] = {
    for {
      client <- BlazeClientBuilder[F](global).stream
      config <- Stream.fromEither[F](ConfigFactory.load)
      helloWorldAlg = HelloWorld.impl[F]
      clientAlg: ClientAlg[F] = ClientImpl[F](client, config.increase.clientApi)
      fileImportAlg: FileImportAlg[F] = FileImportImpl[F](client, config.increase.fileImport)
      transactor = DBTransactor.build[F]
      paymentDBAlg: PaymentDBAlg[ConnectionIO] = PaymentDBImpl()
      paymentAlg: PaymentAlg[F] = PaymentImpl(paymentDBAlg, transactor)
      fileProcessorAlg: FileProcessorAlg[F] = FileProcessorImpl[F](paymentAlg)

      httpApp = (
        IncreaseBookkeeperRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
        IncreaseBookkeeperRoutes.clientInfoRoutes[F](clientAlg) <+>
        IncreaseBookkeeperRoutes.fileImportRoutes[F](fileImportAlg, fileProcessorAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[F]
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(finalHttpApp)
        .serve
    } yield exitCode
  }.drain
}