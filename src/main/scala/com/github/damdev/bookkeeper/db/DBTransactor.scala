package com.github.damdev.bookkeeper.db

import cats.effect.{Async, Blocker, ContextShift, IO}
import com.github.damdev.bookkeeper.config.{AppSettings, DBConfig}
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

object DBTransactor {

  def build[M[_]: Async: ContextShift](config: DBConfig): Transactor[M] = Transactor.fromDriverManager[M](
      config.driver, config.url, config.user, config.password,
      Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
    )

}
