package com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras.impl

import cats.effect.Sync
import cats.implicits._
import com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras.ClientAlg
import com.github.damdev.increase.bookkeeper.increasebookkeeper.config.ClientApiConfig
import com.github.damdev.increase.bookkeeper.increasebookkeeper.model.ClientInfo
import org.http4s.Method.GET
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.{Header, Headers, InvalidMessageBodyFailure, Request, Uri}

class ClientImpl[F[_] : Sync](httpClient: org.http4s.client.Client[F], config: ClientApiConfig) extends ClientAlg[F] {
  val dsl = new Http4sClientDsl[F] {}

  def getClientInfo(id: String): F[Option[ClientInfo]] = {
    val request: Request[F] = Request(GET, Uri.fromString(config.endpoint + id).right.get,
      headers = Headers.of(Header("Authorization", config.authorization)))
    val r: F[Option[ClientInfo]] = httpClient.expect[ClientInfo](request).map(Option.apply).recover {
      case InvalidMessageBodyFailure(_, _) => None
    }
    r
  }
}

object ClientImpl {
  def apply[F[_]: Sync](httpClient: Client[F], config: ClientApiConfig): ClientAlg[F] = new ClientImpl(httpClient, config)
}