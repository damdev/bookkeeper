package com.github.damdev.bookkeeper

import cats.effect.Sync
import cats.implicits._
import com.github.damdev.bookkeeper.algebras._
import org.http4s.{HttpRoutes, QueryParamDecoder, Response}
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.dsl.Http4sDsl
import com.github.damdev.bookkeeper.model.PaymentStatus
import io.circe.generic.auto._
import com.github.damdev.bookkeeper.circe.Formats._
import org.http4s.dsl.impl.{QueryParamDecoderMatcher, ValidatingQueryParamDecoderMatcher}

object BookkeeperRoutes {

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
          payments <- FP.processAndSave(FI.importFile())
          resp <- Ok(payments)
        } yield resp
    }
  }

  def paymentRoutes[F[_]: Sync](PA: PaymentAlg[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._

    def response(clientId: String, status: PaymentStatus): F[Response[F]] = Ok(PA.summary(clientId, status))

    HttpRoutes.of[F] {
      case GET -> Root / "payments" :? ClientQueryParamMatcher(clientId) +& StatusQueryParamMatcher(status) =>
        status.fold(_ => BadRequest("Invalid payment status, use 'pending' or 'paid'"), s => response(clientId, s))
    }
  }
  def transactionsRoutes[F[_]: Sync](PA: PaymentAlg[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._

    def response(clientId: String): F[Response[F]] = Ok(PA.transactions(clientId))

    HttpRoutes.of[F] {
      case GET -> Root / "transactions" :? ClientQueryParamMatcher(clientId) => response(clientId)
    }
  }

  implicit val statusQueryParamDecoder: QueryParamDecoder[PaymentStatus] =
    QueryParamDecoder[String].map(PaymentStatus.of)
  object StatusQueryParamMatcher extends ValidatingQueryParamDecoderMatcher[PaymentStatus]("status")
  object ClientQueryParamMatcher extends QueryParamDecoderMatcher[String]("client")

}