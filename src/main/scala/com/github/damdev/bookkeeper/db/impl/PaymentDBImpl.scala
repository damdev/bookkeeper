package com.github.damdev.bookkeeper.db.impl

import java.time.LocalDate

import com.github.damdev.bookkeeper.db.PaymentDBAlg
import com.github.damdev.bookkeeper.model._
import com.github.damdev.bookkeeper.parser.model.{Currency, Discount, Transaction, TransactionType}
import doobie.free.connection.ConnectionIO
import doobie.syntax._
import cats._
import cats.implicits._
import cats.data.OptionT
import cats.implicits._
import doobie.implicits._
import doobie.util.update.Update
import org.slf4j.LoggerFactory

class PaymentDBImpl extends PaymentDBAlg[ConnectionIO] {
  private val logger = LoggerFactory.getLogger(this.getClass)

  override def savePayment(payment: Payment): ConnectionIO[Int] = for {
    payments <- insertPayment(payment)
    _ <- insertDiscounts(payment)
    _ <- insertTransactions(payment)
  } yield payments

  def insertPayment(p: Payment): ConnectionIO[Int] = {
    sql"""insert into payments(id, client_id, date, currency, total_amount, total_discounts, total_with_discounts)
          values (${p.id}, ${p.clientId}, ${p.date}, ${p.currency.name}, ${p.totalAmount}, ${p.totalDiscounts}, ${p.totalWithDiscounts})
         """.update.run
  }

  type TransactionInfo = (String, BigDecimal, String, String)

  def toTransactionInfo(paymentId: String)(t: Transaction): TransactionInfo = (t.id, t.amount, t.`type`.name, paymentId)

  def insertTransactions(p: Payment): ConnectionIO[Int] = {
    val sql = "insert into transactions(id, amount, type, payment_id) values (?, ?, ?, ?)"
    Update[TransactionInfo](sql).updateMany(p.transactions.getOrElse(Nil).map(toTransactionInfo(p.id)))
  }

  type DiscountInfo = (String, BigDecimal, String, String)

  def toDiscountInfo(paymentId: String)(t: Discount): DiscountInfo = (t.id, t.amount, t.`type`.name, paymentId)

  def insertDiscounts(p: Payment): ConnectionIO[Int] = {
    val sql = "insert into discounts(id, amount, type, payment_id) values (?, ?, ?, ?)"
    Update[DiscountInfo](sql).updateMany(p.discounts.getOrElse(Nil).map(toDiscountInfo(p.id)))
  }

  def existsPayment(p: Payment): ConnectionIO[Boolean] = {
    sql"select 1 from payments where id = ${p.id}".query[Int].option.map(_.nonEmpty)
  }

  override def savePaymentIfNotExists(payment: Payment): ConnectionIO[Option[Int]] = {
    val maybeInsert = for {
      _ <- OptionT.liftF(existsPayment(payment)).filter(b => !b)
      payments <- OptionT.liftF(insertPayment(payment))
      _ <- OptionT.liftF(insertDiscounts(payment))
      _ <- OptionT.liftF(insertTransactions(payment))
    } yield payments
    maybeInsert.value
  }

  override def findForClientAndBeforePaymentDate(clientId: String, date: LocalDate): ConnectionIO[List[Payment]] = {
    sql"select id, client_id, date, currency, total_amount, total_discounts, total_with_discounts from payments where client_id = ${clientId} and date < $date"
      .query[(String, String, LocalDate, String, BigDecimal, BigDecimal, BigDecimal)]
      .map{ t =>
        val (id, clientId, date, currency, totalAmount, totalDiscounts, totalWithDiscounts) = t
        Payment(id, clientId, date, Currency.parse(currency).get, totalAmount, totalDiscounts, totalWithDiscounts, None, None)
      }.to[List]
  }

  override def findForClientAndAfterPaymentDate(clientId: String, date: LocalDate): ConnectionIO[List[Payment]] = {
    sql"select id, client_id, date, currency, total_amount, total_discounts, total_with_discounts from payments where client_id = ${clientId} and date >= $date"
    .query[(String, String, LocalDate, String, BigDecimal, BigDecimal, BigDecimal)]
    .map{ t =>
      val (id, clientId, date, currency, totalAmount, totalDiscounts, totalWithDiscounts) = t
      Payment(id, clientId, date, Currency.parse(currency).get, totalAmount, totalDiscounts, totalWithDiscounts, None, None)
    }.to[List]
  }

  override def findTransactionsForClient(clientId: String): ConnectionIO[List[Transaction]] = {
    sql"select t.id, t.amount, t.type from transactions t inner join payments p on (t.payment_id = p.id) where p.client_id = ${clientId}"
      .query[(String, BigDecimal, String)]
      .map{ t =>
        val (id, amount, tt) = t
        Transaction(id, amount, TransactionType.parse(tt).get)
      }.to[List]
  }

}

object PaymentDBImpl {
  def apply(): PaymentDBImpl = new PaymentDBImpl()
}