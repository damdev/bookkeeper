package com.github.damdev.bookkeeper

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]) =
    BookkeeperServer.stream[IO].compile.drain.as(ExitCode.Success)
}