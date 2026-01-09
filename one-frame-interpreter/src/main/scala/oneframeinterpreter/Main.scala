package oneframeinterpreter

import oneframeinterpreter.config.AppConfig
import oneframeinterpreter.model.RateRequest
import oneframeinterpreter.oneframe.internal.{CurrencyRateStorage, OneFrameClient}
import oneframeinterpreter.oneframe.OneFrameService
import zio.*
import zio.Console.*
import zio.http.ZClient

object Main extends ZIOAppDefault {

  def run: ZIO[ZIOAppArgs & Scope, Any, Any] = myAppLogic.provide(
    AppConfig.layer,
    CurrencyRateStorage.layer,
    OneFrameService.layer,
    ZClient.default
  )

  private val myAppLogic =
    for {
      appConfig <- AppConfig.loadConfig
      oneFrameService <- ZIO.service[OneFrameService]
      _ <- printLine(s"AppConfig Loaded: $appConfig")
      _    <- printLine("Welcome to the One Frame Live Interpreter")
      fromCurr <- readLine("What currency do you want to convert FROM: ")
      toCurr <- readLine("What currency do you want to convert TO: ")
      result <- oneFrameService.getRate(fromCurr, toCurr)
      _ <- printLine(result)
    } yield ()
}