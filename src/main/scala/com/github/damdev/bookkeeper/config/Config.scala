package com.github.damdev.bookkeeper.config

import io.circe
import io.circe.config.parser
import io.circe.generic.auto._
import io.circe.config.syntax._

import scala.concurrent.duration.FiniteDuration


case class AppSettings(increase: IncreaseConfig)
case class IncreaseConfig(clientApi: ClientApiConfig, fileImport: FileImportConfig)
case class ClientApiConfig(endpoint: String, authorization: String)
case class FileImportConfig(interval: FiniteDuration, fileUri: String, authorization: String)

object ConfigFactory {

  def load: Either[circe.Error, AppSettings] = parser.decode[AppSettings]()
}