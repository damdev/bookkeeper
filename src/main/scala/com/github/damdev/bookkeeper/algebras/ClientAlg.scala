package com.github.damdev.bookkeeper.algebras

import com.github.damdev.bookkeeper.model.ClientInfo

trait ClientAlg[F[_]]{
  def getClientInfo(id: String): F[Option[ClientInfo]]
}
