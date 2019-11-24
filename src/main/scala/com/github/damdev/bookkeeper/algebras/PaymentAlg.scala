package com.github.damdev.bookkeeper.algebras

import com.github.damdev.bookkeeper.model.Payment

trait PaymentAlg[F[_]] {

  def savePayment(payment: Payment): F[Payment]
}
