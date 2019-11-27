package com.github.damdev.bookkeeper.circe

import com.github.damdev.bookkeeper.parser.model._
import io.circe.Printer
import org.http4s.circe.CirceInstances

object Formats extends CirceInstances {
  import io.circe.generic.extras.semiauto._
  implicit val currencyEncoder = deriveEnumerationEncoder[Currency]
  implicit val transactionTypeEncoder = deriveEnumerationEncoder[TransactionType]
  implicit val discountTypeEncoder = deriveEnumerationEncoder[DiscountType]
  override protected def defaultPrinter: Printer = Printer.noSpaces.copy(dropNullValues=true)
}
