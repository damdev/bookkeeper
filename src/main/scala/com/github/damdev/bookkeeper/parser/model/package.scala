package com.github.damdev.bookkeeper.parser

import java.time.LocalDate

package object model {

  sealed trait Currency {
    def productPrefix: String
    def name = productPrefix
  }
  case object ARS extends Currency
  case object USD extends Currency
  object Currency {
    def parse(s: String) = s match {
      case "ARS" => Some(ARS)
      case "USD" => Some(USD)
      case _ => None
    }
  }

  sealed trait Record

  case class Header(paymentId: String,
                    currency: Currency,
                    totalAmount: BigDecimal,
                    totalDiscounts: BigDecimal,
                    totalWithDiscounts: BigDecimal) extends Record

  sealed trait TransactionType {
    def productPrefix: String
    def name = productPrefix
  }
  case object Accepted extends TransactionType
  case object Rejected extends TransactionType
  object TransactionType {
    def parse(s: String): Option[TransactionType] = s match {
      case "Accepted" => Some(Accepted)
      case "Rejected" => Some(Rejected)
      case _ => None
    }
  }

  case class Transaction(id: String, amount: BigDecimal, `type`: TransactionType) extends Record

  sealed trait DiscountType {
    def productPrefix: String
    def name = productPrefix
  }
  case object IVA extends DiscountType
  case object Retention extends DiscountType
  case object Commission extends DiscountType
  case object ExtraCharge extends DiscountType
  case object IIBB extends DiscountType
  object DiscountType {
    def parse(s: String): Option[DiscountType] = s match {
      case "IVA" => Some(IVA)
      case "Retention" => Some(Retention)
      case "Commission" => Some(Commission)
      case "ExtraCharge" => Some(ExtraCharge)
      case "IIBB" => Some(IIBB)
      case _ => None
    }
  }

  case class Discount(id: String, amount: BigDecimal, `type`: DiscountType) extends Record

  case class Footer(paymentDate: LocalDate, clientId: String) extends Record

}
