package com.github.damdev.bookkeeper.algebras

import com.github.damdev.bookkeeper.model.Payment
import com.github.damdev.bookkeeper.parser.model.Record

trait FileProcessorAlg[F[_]] {

  def process(records: F[Seq[Either[String, Record]]]): F[Seq[Payment]]

  def processAndSave(records: F[Seq[Either[String, Record]]]): F[Seq[Payment]]
}

