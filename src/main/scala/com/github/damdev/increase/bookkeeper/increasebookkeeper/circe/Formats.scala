package com.github.damdev.increase.bookkeeper.increasebookkeeper.circe

import com.github.damdev.increase.bookkeeper.increasebookkeeper.parser.model._

object Formats {
  import io.circe.generic.extras.semiauto._
  implicit val currencyEncoder = deriveEnumerationEncoder[Currency]
  implicit val transactionTypeEncoder = deriveEnumerationEncoder[TransactionType]
  implicit val discountTypeEncoder = deriveEnumerationEncoder[DiscountType]

}
