package oneframeinterpreter

import oneframeinterpreter.config.AppConfig
import oneframeinterpreter.model.RateRequest
import zio.*
import zio.http.*

object OneFrameClient {
  def getRates(appConfig: AppConfig)(rateRequest: RateRequest): ZIO[Client, Throwable, String] = {
    Client
      .batched(Request.get(s"${appConfig.oneframeURL}/${appConfig.ratesEndpoint}?from=${rateRequest.from}&to=${rateRequest.to}"))
      .flatMap(_.body.asString)
  }
}
