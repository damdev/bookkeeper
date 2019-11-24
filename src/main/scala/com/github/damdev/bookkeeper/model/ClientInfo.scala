package com.github.damdev.bookkeeper.model

import cats.Applicative
import cats.effect.Sync
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.{EntityDecoder, EntityEncoder}

final case class ClientInfo(id: String,
                            email: String,
                            first_name: String,
                            last_name: String,
                            job: String,
                            country: String,
                            address: String,
                            zip_code: String,
                            phone: String)
object ClientInfo {
  implicit val clientDecoder: Decoder[ClientInfo] = deriveDecoder[ClientInfo]
  implicit def clientEntityDecoder[F[_]: Sync]: EntityDecoder[F, ClientInfo] =
    jsonOf
  implicit val clientEncoder: Encoder[ClientInfo] = deriveEncoder[ClientInfo]
  implicit def clientEntityEncoder[F[_]: Applicative]: EntityEncoder[F, ClientInfo] =
    jsonEncoderOf
}

final case class ClientError(e: Throwable) extends RuntimeException
