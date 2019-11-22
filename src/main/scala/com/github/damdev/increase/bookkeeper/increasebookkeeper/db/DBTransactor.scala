package com.github.damdev.increase.bookkeeper.increasebookkeeper.db

import cats.effect.{Async, Blocker, ContextShift, IO}
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

object DBTransactor {

  def build[M[_]: Async: ContextShift]: Transactor[M] = Transactor.fromDriverManager[M](
      "org.mariadb.jdbc.Driver",     // driver classname
      "jdbc:mariadb://127.0.0.1:33060/bookkeeper?serverTimezone=UTC",     // connect URL (driver-specific)
      "bookkeeper",                  // user
      "secret",                          // password
      Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
    )

}
