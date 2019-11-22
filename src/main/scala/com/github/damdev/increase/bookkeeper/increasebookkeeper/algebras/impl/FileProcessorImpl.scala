package com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras.impl

import cats.effect.Sync
import cats.implicits._
import com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras.{FileProcessorAlg, PaymentAlg}
import com.github.damdev.increase.bookkeeper.increasebookkeeper.model.{Payment, PaymentBuilder}
import com.github.damdev.increase.bookkeeper.increasebookkeeper.parser.model._
import org.slf4j.LoggerFactory

class FileProcessorImpl[F[_]: Sync](paymentAlg: PaymentAlg[F]) extends FileProcessorAlg[F] {
  private val logger = LoggerFactory.getLogger(this.getClass)

  override def process(records: F[Seq[Either[String, Record]]]): F[Seq[Payment]] = {
    records.map(processRecords)
  }

  override def processAndSave(records: F[Seq[Either[String, Record]]]): F[Seq[Payment]] = {
    val builded: F[Seq[Payment]] = records.map(processRecords)
    builded.flatMap(ps => ps.toList.map(paymentAlg.savePayment).sequence.map(_.toSeq))
  }

  private def processRecords(s: Seq[Either[String, Record]]): Seq[Payment] = {
      s.foldLeft[(PaymentBuilder, List[Payment])](PaymentBuilder() -> Nil){ (acum, i) =>
        i match {
          case Right(h: Header) => acum._1.withHeader(h) -> acum._2
          case Right(tx: Transaction) => acum._1.withTransaction(tx) -> acum._2
          case Right(ds: Discount) => acum._1.withDiscount(ds) -> acum._2
          case Right(f: Footer) => PaymentBuilder() -> acum._1.withFooter(f).build.fold(acum._2)(p => p :: acum._2)
          case Left(error) =>
            logger.warn(s"Ignoring line with error: ${error}")
            acum
        }
      }
  }._2
}

object FileProcessorImpl {
  def apply[F[_]: Sync](paymentAlg: PaymentAlg[F]): FileProcessorAlg[F] = new FileProcessorImpl(paymentAlg)
}