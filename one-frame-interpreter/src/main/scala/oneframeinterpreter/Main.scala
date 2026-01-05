package oneframeinterpreter

import oneframeinterpreter.config.AppConfig
import oneframeinterpreter.model.RateRequest
import zio.*
import zio.Console.*
import zio.http.Client

object Main extends ZIOAppDefault {

  def run: ZIO[ZIOAppArgs & Scope, Any, Any] = myAppLogic.provide(
    Client.default
  )

  private val myAppLogic =
    for {
      appConfig <- AppConfig.loadConfig
      _ <- printLine(s"AppConfig Loaded: $appConfig")
      _    <- printLine("Welcome to the One Frame Live Interpreter")
      fromCurr <- readLine("What currency do you want to convert FROM: ")
      toCurr <- readLine("What currency do you want to convert TO: ")
      rateRequest <- RateRequest.fromStrings(fromCurr, toCurr)
      result <- OneFrameClient.getRates(appConfig)(rateRequest)
      _ <- printLine(result)
    } yield ()
}