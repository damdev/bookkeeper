package com.github.damdev.bookkeeper.db

import com.github.damdev.bookkeeper.model.Payment

trait PaymentDBAlg[F[_]] {
  def savePayment(payment: Payment): F[Int]
}
