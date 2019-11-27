package com.github.damdev.bookkeeper

import java.time.LocalDate

import com.github.damdev.bookkeeper.parser.model._

package object model {

  trait PaymentStatus
  object PaymentStatus {
    def of(s: String): PaymentStatus = {
      s.toLowerCase match {
        case "pending" => Pending
        case "paid" => Paid
      }
    }
  }
  case object Pending extends PaymentStatus
  case object Paid extends PaymentStatus

  case class Payment(id: String,
                     clientId: String,
                     date: LocalDate,
                     currency: Currency,
                     totalAmount: BigDecimal,
                     totalDiscounts: BigDecimal,
                     totalWithDiscounts: BigDecimal,
                     transactions: Option[List[Transaction]],
                     discounts: Option[List[Discount]])

  case class PaymentBuilder(header: Option[Header] = None, transactions: List[Transaction] = Nil, discounts: List[Discount] = Nil, footer: Option[Footer] = None) {
    def withHeader(h: Header) = this.copy(header = Some(h))
    def withFooter(f: Footer) = this.copy(footer = Some(f))
    def withTransaction(t: Transaction) = this.copy(transactions = t :: transactions)
    def withDiscount(d: Discount) = this.copy(discounts = d :: discounts)

    def build: Option[Payment] = {
      for {
        h <- header
        f <- footer
      } yield Payment(h.paymentId, f.clientId, f.paymentDate, h.currency, h.totalAmount, h.totalDiscounts, h.totalWithDiscounts, Some(transactions), Some(discounts))
    }
  }

  object PaymentBuilder {
    def apply(): PaymentBuilder = new PaymentBuilder()
  }
}
