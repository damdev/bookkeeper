package com.github.damdev.bookkeeper.db

import java.time.LocalDate

import com.github.damdev.bookkeeper.model.Payment

trait PaymentDBAlg[F[_]] {
  def findForClientAndBeforePaymentDate(clientId: String, date: LocalDate): F[List[Payment]]
  def findForClientAndAfterPaymentDate(clientId: String, date: LocalDate): F[List[Payment]]

  def savePayment(payment: Payment): F[Int]
  def savePaymentIfNotExists(payment: Payment): F[Option[Int]]
}
