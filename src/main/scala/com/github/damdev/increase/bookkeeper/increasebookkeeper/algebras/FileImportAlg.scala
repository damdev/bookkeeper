package com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras

import com.github.damdev.increase.bookkeeper.increasebookkeeper.parser.model.Record

trait FileImportAlg[F[_]] {
  def importFile(): F[Seq[Either[String, Record]]]
}