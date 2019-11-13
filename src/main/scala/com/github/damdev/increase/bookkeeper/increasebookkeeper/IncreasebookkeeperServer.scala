package com.github.damdev.increase.bookkeeper.increasebookkeeper

import cats.effect.{ConcurrentEffect, ContextShift, Timer}
import cats.implicits._
import com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras.ClientAlg
import com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras.impl.ClientAlgImpl
import com.github.damdev.increase.bookkeeper.increasebookkeeper.config.ConfigFactory
import fs2.Stream
import org.http4s.client.blaze.BlazeClientBuilder
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
      clientAlg: ClientAlg[F] = ClientAlgImpl[F](client, config.increase.clientApi)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      httpApp = (
        IncreaseBookkeeperRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
        IncreaseBookkeeperRoutes.clientInfoRoutes[F](clientAlg)
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