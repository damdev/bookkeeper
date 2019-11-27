package com.github.damdev.bookkeeper.algebras

import com.github.damdev.bookkeeper.model
import com.github.damdev.bookkeeper.model.Payment
import com.github.damdev.bookkeeper.parser.model.Transaction

case class PaymentTotals(totalAmount: BigDecimal, totalDiscounts: BigDecimal, totalWithDiscounts: BigDecimal) {
  def acumulate(payment: Payment): PaymentTotals = {
    this.copy(
      totalAmount = totalAmount + payment.totalAmount,
      totalDiscounts = totalDiscounts + payment.totalDiscounts,
      totalWithDiscounts = totalWithDiscounts + payment.totalWithDiscounts)
}
}
case class PaymentsSummary(totals: Map[String, PaymentTotals]) extends AnyVal

trait PaymentAlg[F[_]] {
  def summary(clientId: String, status: model.PaymentStatus): F[PaymentsSummary]
  def transactions(clientId: String): F[List[Transaction]]


  def savePayment(payment: Payment): F[Option[Payment]]
}
