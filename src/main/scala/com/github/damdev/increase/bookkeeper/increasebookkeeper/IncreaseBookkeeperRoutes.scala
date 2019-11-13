package com.github.damdev.increase.bookkeeper.increasebookkeeper

import cats.effect.Sync
import cats.implicits._
import com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras.ClientAlg
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

object IncreaseBookkeeperRoutes {

  def clientInfoRoutes[F[_]: Sync](CA: ClientAlg[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "clients" / id =>
        for {
          clientInfo <- CA.getClientInfo(id)
          resp <- clientInfo.map(ci => Ok(ci)).getOrElse(NotFound("There is no client with this id."))
        } yield resp
    }
  }

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorld.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }
  }
}