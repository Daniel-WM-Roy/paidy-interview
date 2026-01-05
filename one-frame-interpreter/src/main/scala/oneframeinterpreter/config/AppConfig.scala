package oneframeinterpreter.config

import zio.config.typesafe.*
import zio.{Config, ConfigProvider}

import scala.io.Source

case class AppConfig(
                    oneframeURL: String,
                    ratesEndpoint: String,
                    currencyTTLSeconds: Int
                    )

object AppConfig {

  import zio.config._
  import zio.config.magnolia._

  private val configDescriptor: Config[AppConfig] = deriveConfig[AppConfig]

  val loadConfig: zio.IO[Config.Error, AppConfig] = {
    val confStr = Source.fromResource("app-config.conf").getLines.mkString
    ConfigProvider
        .fromHoconString(confStr)
        .load(AppConfig.configDescriptor)
  }
}
