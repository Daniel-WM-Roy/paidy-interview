package oneframeinterpreter.oneframe

import oneframeinterpreter.config.AppConfig
import oneframeinterpreter.model.RateRequest
import oneframeinterpreter.oneframe.internal.{CurrencyRateStorage, OneFrameClient}
import zio.{ZIO, ZLayer}
import zio.http.Client

import java.time.Instant

object OneFrameService {
  val layer: ZLayer[AppConfig & CurrencyRateStorage & Client, Nothing, OneFrameService] = ZLayer.fromFunction(OneFrameService(_, _, _))
}

class OneFrameService(appConfig: AppConfig, storage: CurrencyRateStorage, client: Client) {

  private def getRateFromClient = OneFrameClient.requestRate(appConfig)(_)

  private def parseToEpochSecond(timestamp: String): Long =
    Instant.parse(timestamp).getEpochSecond

  def getRate(from: String, to: String): ZIO[Any, Serializable, Int] =
    for {
      rateRequest <- RateRequest.fromStrings(from, to)
      maybeStoredRate <- storage.getCurrencyRate(rateRequest.from, rateRequest.to)
      finalRate <- ZIO.fromOption(maybeStoredRate).orElse {
        for {
          oneFrameResponse <- getRateFromClient(rateRequest).provide(ZLayer.succeed(client))
          timestamp = parseToEpochSecond(oneFrameResponse.timestamp)
          _ <- storage.setCurrencyRate(rateRequest.from, rateRequest.to, oneFrameResponse.price, timestamp)
        } yield oneFrameResponse.price
      }
    } yield finalRate

}
