package com.github.damdev.increase.bookkeeper.increasebookkeeper

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]) =
    IncreasebookkeeperServer.stream[IO].compile.drain.as(ExitCode.Success)
}