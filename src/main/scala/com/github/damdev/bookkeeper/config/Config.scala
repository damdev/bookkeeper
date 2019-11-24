package com.github.damdev.bookkeeper.config

import io.circe
import io.circe.config.parser
import io.circe.generic.auto._
import io.circe.config.syntax._

import scala.concurrent.duration.FiniteDuration


case class AppSettings(bookkeeper: BookKeeperConfig)
case class BookKeeperConfig(clientApi: ClientApiConfig, fileImport: FileImportConfig, db: DBConfig)
case class ClientApiConfig(endpoint: String, authorization: String)
case class FileImportConfig(interval: FiniteDuration, fileUri: String, authorization: String)
case class DBConfig(driver: String, url: String, user: String, password: String)

object ConfigFactory {
  def load: Either[circe.Error, AppSettings] = parser.decode[AppSettings]()
}