package oneframeinterpreter.oneframe.internal

import oneframeinterpreter.config.AppConfig
import oneframeinterpreter.model.CurrencyName
import zio.{Clock, ZIO, ZLayer}

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import scala.collection.mutable

object CurrencyRateStorage {
  val layer: ZLayer[AppConfig, Nothing, CurrencyRateStorage] = ZLayer.fromFunction(CurrencyRateStorage(_))
}

class CurrencyRateStorage(appConfig: AppConfig) {

  private val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  private val db: mutable.Map[String, (Int, Long)] = new mutable.HashMap()

  def setCurrencyRate(from: CurrencyName, to: CurrencyName, value: Int, timeSecs: Long): ZIO[Any, Nothing, Boolean] = {
    val key = s"$from$to"
    ZIO.succeed {
      db.addOne((key, (value, timeSecs)))
      true
    }
  }

  def getCurrencyRate(from: CurrencyName, to: CurrencyName): ZIO[Any, Nothing, Option[Int]] = {
    for {
      currentTimeSecs <- Clock.currentTime(TimeUnit.SECONDS)
    } yield {
      val key = s"$from$to"
      db.get(key).flatMap((rate, timestamp) => {
        if (currentTimeSecs - timestamp > appConfig.currencyTTLSeconds) {
          db.remove(key)
          Option.empty
        } else {
          Option.apply(rate)
        }
      })
    }
  }
}
