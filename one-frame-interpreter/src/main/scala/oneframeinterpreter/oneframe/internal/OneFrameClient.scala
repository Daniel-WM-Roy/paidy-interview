package oneframeinterpreter.oneframe.internal

import oneframeinterpreter.config.AppConfig
import oneframeinterpreter.model.RateRequest
import zio.*
import zio.http.*
import zio.json.*

object OneFrameClient {
  def requestRate(appConfig: AppConfig)(rateRequest: RateRequest): ZIO[Client, Serializable, OneFrameResponse] = {
    val url = s"${appConfig.oneframeURL}/${appConfig.ratesEndpoint}?from=${rateRequest.from}&to=${rateRequest.to}"
    for {
      response <- Client.batched(Request.get(url))
      responseAsStr <- response.body.asString
      ofr <- ZIO.fromEither(responseAsStr.fromJson[OneFrameResponse])
    } yield ofr
  }
}
