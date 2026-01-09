package oneframeinterpreter.oneframe

import oneframeinterpreter.config.AppConfig
import oneframeinterpreter.model.RateRequest
import oneframeinterpreter.oneframe.internal.{CurrencyRateStorage, OneFrameClient}
import zio.{ZIO, ZLayer}
import zio.http.Client

import java.time.Instant

object OneFrameService {
  val layer: ZLayer[AppConfig & CurrencyRateStorage, Nothing, OneFrameService] = ZLayer.fromFunction(OneFrameService(_, _))
}

class OneFrameService(appConfig: AppConfig, storage: CurrencyRateStorage) {
  
  private def getRateFromClient = OneFrameClient.requestRate(appConfig)(_)

  private def parseTimestamp(timestamp: String): Long =
    Instant.parse(timestamp).getEpochSecond
  
  def getRate(from: String, to: String): ZIO[Client & CurrencyRateStorage, Serializable, Int] =
    for {
      storage <- ZIO.service[CurrencyRateStorage]
      rateRequest <- RateRequest.fromStrings(from, to)
      maybeStoredRate <- storage.getCurrencyRate(rateRequest.from, rateRequest.to)
      finalRate <- ZIO.fromOption(maybeStoredRate).orElse {
        for {
          oneFrameResponse <- getRateFromClient(rateRequest)
          timestamp = parseTimestamp(oneFrameResponse.timestamp)
          _ <- storage.setCurrencyRate(rateRequest.from, rateRequest.to, oneFrameResponse.price, timestamp)
        } yield oneFrameResponse.price
      }
    } yield finalRate
  
}
