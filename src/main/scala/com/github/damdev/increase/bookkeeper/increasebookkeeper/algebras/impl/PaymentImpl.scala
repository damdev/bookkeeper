package com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras.impl

import cats.effect.Sync
import com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras.PaymentAlg
import com.github.damdev.increase.bookkeeper.increasebookkeeper.db.PaymentDBAlg
import com.github.damdev.increase.bookkeeper.increasebookkeeper.model.Payment
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.util.transactor.Transactor
import cats.implicits._

class PaymentImpl[F[_]: Sync](paymentDBAlg: PaymentDBAlg[ConnectionIO], transactor: Transactor[F]) extends PaymentAlg[F] {
  override def savePayment(payment: Payment): F[Payment] = {
    paymentDBAlg.savePayment(payment).transact(transactor).map(_ => payment)
  }
}

object PaymentImpl {
  def apply[F[_]: Sync](paymentDBAlg: PaymentDBAlg[ConnectionIO], transactor: Transactor[F]): PaymentAlg[F] =
    new PaymentImpl(paymentDBAlg, transactor)
}