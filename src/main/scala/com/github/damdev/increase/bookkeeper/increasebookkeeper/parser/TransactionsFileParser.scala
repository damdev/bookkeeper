package com.github.damdev.increase.bookkeeper.increasebookkeeper.parser
import java.time.LocalDate
import java.time.temporal.ChronoField.DAY_OF_MONTH
import java.time.temporal.ChronoField.MONTH_OF_YEAR
import java.time.temporal.ChronoField.YEAR

import atto.Atto._
import atto._
import cats.implicits._
import com.github.damdev.increase.bookkeeper.increasebookkeeper.parser.model._

object TransactionsFileParser {

  def parse(line: String): Either[String, Record] = recordTypeParser.parseOnly(line).either.leftMap(l => s"Error $l for line $line")

  private val recordTypeParser: Parser[Record] = digit.collect {
    case '1' => headerParser
    case '2' => transactionParser
    case '3' => discountParser
    case '4' => footerParser
    case _ => err("Invalid record type")
  }.flatten

  private val currencyParser: Parser[Currency] = take(3).collect {
    case "000" => ok(ARS)
    case "001" => ok(USD)
    case _ => err[Currency]("Invalid Currency")
  }.flatten

  private val amountParser: Parser[BigDecimal] = count(13, digit)
    .map(_.mkString)
    .map((s: String) => BigDecimal(s) / 100)

  private val headerParser: Parser[Header] = for {
    paymentId <- take(32)
    _ <- take(3)
    currency <- currencyParser
    totalAmount <- amountParser
    totalDiscounts <- amountParser
    totalWithDiscounts <- amountParser
  } yield Header(paymentId, currency, totalAmount, totalDiscounts, totalWithDiscounts)

  private val transactionTypeParser: Parser[TransactionType] = digit.collect {
    case '1' => ok(Accepted)
    case '2' => ok(Rejected)
    case _ => err[TransactionType]("Invalid TransactionType")
  }.flatten

  private val transactionParser: Parser[Transaction] = for {
    id <- take(32)
    amount <- amountParser
    _ <- take(5)
    tipe <- transactionTypeParser
  } yield Transaction(id, amount, tipe)

  // In the doc, says 1-5 but in the file in the api, the values are 0-4
  private val discountTypeParser: Parser[DiscountType] = digit.collect {
    case '0' => ok(IVA)
    case '1' => ok(Retention)
    case '2' => ok(Commission)
    case '3' => ok(ExtraCharge)
    case '4' => ok(IIBB)
    case _ => err[DiscountType]("Invalid DiscountType")
  }.flatten

  private val discountParser: Parser[Discount] = for {
    id <- take(32)
    amount <- amountParser
    _ <- take(3)
    tipe <- discountTypeParser
  } yield Discount(id, amount, tipe)

  private val dateParser: Parser[LocalDate] = for {
    year <- count(4, digit).map(_.mkString).map(_.toLong).filter(YEAR.range().isValidValue)
    month <- count(2, digit).map(_.mkString).map(_.toLong).filter(MONTH_OF_YEAR.range().isValidValue)
    day <- count(2, digit).map(_.mkString).map(_.toLong).filter(DAY_OF_MONTH.range().isValidValue)
  } yield LocalDate.of(year.intValue(), month.intValue(), day.intValue())

  private val footerParser: Parser[Footer] = for {
    _ <- take(15)
    paymentDate <- dateParser
    clientId <- take(32)
  } yield Footer(paymentDate, clientId)

}
