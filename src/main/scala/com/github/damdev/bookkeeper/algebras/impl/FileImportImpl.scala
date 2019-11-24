package com.github.damdev.bookkeeper.algebras.impl

import cats.effect.Sync
import cats.implicits._
import com.github.damdev.bookkeeper.algebras.FileImportAlg
import com.github.damdev.bookkeeper.config.FileImportConfig
import com.github.damdev.bookkeeper.parser.TransactionsFileParser
import com.github.damdev.bookkeeper.parser.model.Record
import org.http4s.client.Client
import org.http4s.{Header, Headers, Method, Request, Uri}
import org.slf4j.LoggerFactory

class FileImportImpl[F[_]: Sync](httpClient: org.http4s.client.Client[F], config: FileImportConfig) extends FileImportAlg[F] {

  private val logger = LoggerFactory.getLogger(this.getClass)

  override def importFile(): F[Seq[Either[String, Record]]] = {
    val request: Request[F] = Request(Method.GET, Uri.fromString(config.fileUri).right.get, headers = Headers.of(Header("Authorization", config.authorization)))
    val response = httpClient.expect[String](request)
    response.map(_.split('\n').map(TransactionsFileParser.parse))
  }
}

object FileImportImpl {
  def apply[F[_]: Sync](httpClient: Client[F], config: FileImportConfig): FileImportAlg[F] = new FileImportImpl(httpClient, config)
}