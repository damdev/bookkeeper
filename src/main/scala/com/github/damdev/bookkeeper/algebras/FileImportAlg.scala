package com.github.damdev.bookkeeper.algebras

import com.github.damdev.bookkeeper.parser.model.Record


trait FileImportAlg[F[_]] {
  def importFile(): F[Seq[Either[String, Record]]]
}