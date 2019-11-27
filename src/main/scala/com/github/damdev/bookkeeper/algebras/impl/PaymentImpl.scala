package com.github.damdev.bookkeeper.algebras.impl

import java.time.LocalDate

import cats.effect.Sync
import com.github.damdev.bookkeeper.algebras.{PaymentAlg, PaymentTotals, PaymentsSummary}
import com.github.damdev.bookkeeper.db.PaymentDBAlg
import com.github.damdev.bookkeeper.model.{Paid, Payment, Pending}
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor
import cats.implicits._
import com.github.damdev.bookkeeper.model._
import com.github.damdev.bookkeeper.parser.model._
import org.slf4j.LoggerFactory

class PaymentImpl[F[_]: Sync](paymentDBAlg: PaymentDBAlg[ConnectionIO], transactor: Transactor[F]) extends PaymentAlg[F] {
  private val logger = LoggerFactory.getLogger(this.getClass)

  override def savePayment(payment: Payment): F[Option[Payment]] = {
    paymentDBAlg.savePaymentIfNotExists(payment).transact(transactor).map {
      case Some(_) => Some(payment)
      case None =>
        logger.warn(s"Ignored payment ${payment.id} because is already processed.")
        None
    }
  }

  def calculateTotals(payments: List[Payment]): PaymentTotals = payments.foldLeft(PaymentTotals(0, 0, 0)) {
    (total, payment) => total.acumulate(payment)
  }

  private def buildSummary(ps: List[Payment]): PaymentsSummary = {
    val (arsPayments, usdPayments) = ps.partition(_.currency == ARS)
    PaymentsSummary(Map(ARS.toString -> calculateTotals(arsPayments), USD.toString -> calculateTotals(usdPayments)))
  }

  override def summary(clientId: String, status: PaymentStatus): F[PaymentsSummary] = {
    val ps = status match {
      case Pending => paymentDBAlg.findForClientAndAfterPaymentDate(clientId, LocalDate.now())
      case Paid => paymentDBAlg.findForClientAndBeforePaymentDate(clientId, LocalDate.now())
    }
    ps.transact(transactor).map(buildSummary)
  }

  override def transactions(clientId: String): F[List[Transaction]] = {
    paymentDBAlg.findTransactionsForClient(clientId).transact(transactor)
  }
}

object PaymentImpl {
  def apply[F[_]: Sync](paymentDBAlg: PaymentDBAlg[ConnectionIO], transactor: Transactor[F]): PaymentAlg[F] =
    new PaymentImpl(paymentDBAlg, transactor)
}