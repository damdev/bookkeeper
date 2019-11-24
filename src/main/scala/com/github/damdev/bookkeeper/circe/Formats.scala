package com.github.damdev.bookkeeper.circe

import com.github.damdev.bookkeeper.algebras.PaymentsSummary
import com.github.damdev.bookkeeper.parser.model._

object Formats {
  import io.circe.generic.extras.semiauto._
  implicit val currencyEncoder = deriveEnumerationEncoder[Currency]
  implicit val transactionTypeEncoder = deriveEnumerationEncoder[TransactionType]
  implicit val discountTypeEncoder = deriveEnumerationEncoder[DiscountType]

}
