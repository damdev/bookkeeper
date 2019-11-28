package com.github.damdev.bookkeeper

import cats.effect.{Concurrent, ContextShift, Fiber, IO, Timer}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._
import fs2.Stream
import cats._
import implicits._
import org.mariadb.jdbc.internal.logging.LoggerFactory

trait CronAlg[F[_]] {
  def cron[A](duration: FiniteDuration)(io: F[A])
             (implicit concurrent: Concurrent[F],
              timer: Timer[F],
              compiler: Stream.Compiler[F, F]): Stream[F, Unit]

  def delay[A](a: => A): F[A]

}

class Cron extends CronAlg[IO] {
  private val logger = LoggerFactory.getLogger(this.getClass)

  def cron[A](duration: FiniteDuration)(io: IO[A])
             (implicit concurrent: Concurrent[IO],
              timer: Timer[IO],
              compiler: Stream.Compiler[IO, IO]): Stream[IO, Unit] = {
    (
      Stream.emit(logger.info("Starting cron...")).covary[IO] ++
      Stream.sleep(5 seconds)).concurrently(
        Stream.awakeEvery[IO](duration).flatMap(_ =>
          Stream.emit {
            io.unsafeRunSync()
          }
        )
    )
  }

  def delay[A](a: => A): IO[A] = IO.delay(a)

}

object Cron {
  def apply(): Cron = new Cron()
}
