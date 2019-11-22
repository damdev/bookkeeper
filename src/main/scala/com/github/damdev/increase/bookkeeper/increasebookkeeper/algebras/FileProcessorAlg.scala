package com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras

import com.github.damdev.increase.bookkeeper.increasebookkeeper.model.Payment
import com.github.damdev.increase.bookkeeper.increasebookkeeper.parser.model.Record

trait FileProcessorAlg[F[_]] {

  def process(records: F[Seq[Either[String, Record]]]): F[Seq[Payment]]

  def processAndSave(records: F[Seq[Either[String, Record]]]): F[Seq[Payment]]
}

