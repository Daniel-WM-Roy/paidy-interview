package oneframeinterpreter.model

import zio.ZIO

case class RateRequest(from: CurrencyName, to: CurrencyName)

object RateRequest {
  def fromStrings(fromCurr: String, toCurr: String): zio.IO[String, RateRequest] =
    ZIO.fromEither {
      for {
        from <- CurrencyName.from(fromCurr)
        to <- CurrencyName.from(toCurr)
      } yield RateRequest(from, to)
    }
}