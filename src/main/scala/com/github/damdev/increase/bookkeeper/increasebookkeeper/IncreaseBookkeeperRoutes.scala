package com.github.damdev.increase.bookkeeper.increasebookkeeper

import cats.effect.Sync
import cats.implicits._
import com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras._
import io.circe.generic.auto._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityEncoder._
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

  def fileImportRoutes[F[_]: Sync](FI: FileImportAlg[F], FP: FileProcessorAlg[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "file-import" =>
        for {
          parsedFile <- FI.importFile()
          resp <- Ok(parsedFile.flatMap(_.toOption).toList.map(_.toString))
        } yield resp
      case GET -> Root / "file-process" =>
        for {
          payments <- FP.process(FI.importFile())
          resp <- Ok(payments)
        } yield resp
      case GET -> Root / "file-process-save" =>
        for {
          payments <- FP.processAndSave(FI.importFile())
          resp <- Ok(payments)
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