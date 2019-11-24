package com.github.damdev.bookkeeper.db.impl

import com.github.damdev.bookkeeper.db.PaymentDBAlg
import com.github.damdev.bookkeeper.model._
import com.github.damdev.bookkeeper.parser.model.{Discount, Transaction, TransactionType}
import doobie.free.connection.ConnectionIO
import doobie.syntax._
import cats._
import cats.implicits._
import doobie.implicits._
import doobie.util.update.Update

class PaymentDBImpl extends PaymentDBAlg[ConnectionIO] {
  override def savePayment(payment: Payment): ConnectionIO[Int] = for {
    payments <- insertPayment(payment)

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
    Update[TransactionInfo](sql).updateMany(p.transactions.map(toTransactionInfo(p.id)))
  }

  type DiscountInfo = (String, BigDecimal, String, String)

  def toDiscountInfo(paymentId: String)(t: Discount): DiscountInfo = (t.id, t.amount, t.`type`.name, paymentId)

  def insertDiscounts(p: Payment): ConnectionIO[Int] = {
    val sql = "insert into transactions(id, amount, type, payment_id) values (?, ?, ?, ?)"
    Update[DiscountInfo](sql).updateMany(p.discounts.map(toDiscountInfo(p.id)))
  }
}

object PaymentDBImpl {
  def apply(): PaymentDBImpl = new PaymentDBImpl()
}