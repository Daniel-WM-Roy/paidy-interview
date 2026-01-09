package oneframeinterpreter

import oneframeinterpreter.config.AppConfig
import oneframeinterpreter.oneframe.internal.CurrencyRateStorage
import oneframeinterpreter.oneframe.OneFrameService
import zio.*
import zio.Console.*
import zio.http.ZClient

object Main extends ZIOAppDefault {

  def run: ZIO[ZIOAppArgs & Scope, Any, Any] = myApp.provide(
    AppConfig.layer,
    CurrencyRateStorage.layer,
    OneFrameService.layer,
    ZClient.default
  )

  private val greeting =
    for {
      _ <- printLine("Welcome to the One Frame Live Interpreter")
    } yield true

  private val mainLoop =
    for {
      appConfig <- AppConfig.loadConfig
      oneFrameService <- ZIO.service[OneFrameService]
      fromCurr <- readLine("What currency do you want to convert FROM: ")
      toCurr <- readLine("What currency do you want to convert TO: ")
      result <- oneFrameService.getRate(fromCurr, toCurr)
      _ <- printLine(result)
    } yield ()

  private val myApp =
    greeting *> mainLoop.forever
}