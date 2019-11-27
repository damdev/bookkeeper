package com.github.damdev.bookkeeper.db

import java.time.LocalDate

import com.github.damdev.bookkeeper.model.Payment
import com.github.damdev.bookkeeper.parser.model.Transaction

trait PaymentDBAlg[F[_]] {
  def findForClientAndBeforePaymentDate(clientId: String, date: LocalDate): F[List[Payment]]
  def findForClientAndAfterPaymentDate(clientId: String, date: LocalDate): F[List[Payment]]
  def findTransactionsForClient(clientId: String): F[List[Transaction]]

  def savePayment(payment: Payment): F[Int]
  def savePaymentIfNotExists(payment: Payment): F[Option[Int]]
}
