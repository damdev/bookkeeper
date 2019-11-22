package com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras

import com.github.damdev.increase.bookkeeper.increasebookkeeper.model.Payment

trait PaymentAlg[F[_]] {

  def savePayment(payment: Payment): F[Payment]
}
