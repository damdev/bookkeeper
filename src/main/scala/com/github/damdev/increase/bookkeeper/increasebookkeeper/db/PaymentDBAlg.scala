package com.github.damdev.increase.bookkeeper.increasebookkeeper.db

import com.github.damdev.increase.bookkeeper.increasebookkeeper.model.Payment

trait PaymentDBAlg[F[_]] {
  def savePayment(payment: Payment): F[Int]
}
