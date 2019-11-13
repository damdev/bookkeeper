package com.github.damdev.increase.bookkeeper.increasebookkeeper.parser

import java.time.LocalDate

package object model {

  sealed trait Currency
  case object ARS extends Currency
  case object USD extends Currency

  sealed trait Record

  case class Header(paymentId: String,
                    currency: Currency,
                    totalAmount: BigDecimal,
                    totalDiscounts: BigDecimal,
                    totalWithDiscounts: BigDecimal) extends Record

  sealed trait TransactionType
  case object Accepted extends TransactionType
  case object Rejected extends TransactionType

  case class Transaction(id: String, amount: BigDecimal, `type`: TransactionType) extends Record

  sealed trait DiscountType
  case object IVA extends DiscountType
  case object Retention extends DiscountType
  case object Commission extends DiscountType
  case object ExtraCharge extends DiscountType
  case object IIBB extends DiscountType

  case class Discount(id: String, amount: BigDecimal, `type`: DiscountType) extends Record

  case class Footer(paymentDate: LocalDate, clientId: String) extends Record

}
