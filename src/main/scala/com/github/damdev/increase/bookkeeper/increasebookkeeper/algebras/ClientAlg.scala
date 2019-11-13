package com.github.damdev.increase.bookkeeper.increasebookkeeper.algebras

import com.github.damdev.increase.bookkeeper.increasebookkeeper.model.ClientInfo

trait ClientAlg[F[_]]{
  def getClientInfo(id: String): F[Option[ClientInfo]]
}
