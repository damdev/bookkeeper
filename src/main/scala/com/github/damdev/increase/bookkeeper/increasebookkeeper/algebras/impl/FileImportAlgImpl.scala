package com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras.impl

import cats.effect.{Effect, Sync}
import com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras.FileImportAlg
import com.github.damdev.increase.bookkeeper.increasebookkeeper.config.FileImportConfig
import com.github.damdev.increase.bookkeeper.increasebookkeeper.parser.TransactionsFileParser
import com.github.damdev.increase.bookkeeper.increasebookkeeper.parser.model.Record
import org.http4s.{EntityDecoder, Method, Request, Uri}

class FileImportAlgImpl[F[_]: Sync](httpClient: org.http4s.client.Client[F], config: FileImportConfig) extends FileImportAlg[F] {

  override def importFile(): F[Seq[Either[String, Record]]] = {
    val request: Request[F] = Request(Method.GET, Uri.fromString(config.fileUri).right.get)
    val response = httpClient.expect[Seq[Either[String, Record]]](request)(
      EntityDecoder.text.map(_.split('\n').map(TransactionsFileParser.parse))
    )
    response
  }
}
