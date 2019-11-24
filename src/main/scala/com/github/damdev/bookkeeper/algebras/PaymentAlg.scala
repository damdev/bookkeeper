package com.github.damdev.bookkeeper.algebras

import com.github.damdev.bookkeeper.model
import com.github.damdev.bookkeeper.model.Payment
import com.github.damdev.bookkeeper.parser.model.Currency

case class PaymentTotals(totalAmount: BigDecimal, totalDiscounts: BigDecimal, totalWithDiscounts: BigDecimal) {
  def acumulate(payment: Payment): PaymentTotals = {
    this.copy(
      totalAmount = totalAmount + payment.totalAmount,
      totalDiscounts = totalDiscounts + payment.totalDiscounts,
      totalWithDiscounts = totalWithDiscounts + payment.totalWithDiscounts)
}
}
case class PaymentsSummary(payments: List[Payment], totals: Map[String, PaymentTotals])

trait PaymentAlg[F[_]] {
  def summary(clientId: String, status: model.PaymentStatus): F[PaymentsSummary]


  def savePayment(payment: Payment): F[Option[Payment]]
}
