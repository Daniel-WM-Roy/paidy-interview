package oneframeinterpreter.oneframe

import oneframeinterpreter.config.AppConfig
import oneframeinterpreter.oneframe.internal.CurrencyRateStorage
import zio.http.{Client, Method, Request, Response, Routes, TestClient, handler}
import zio.{Clock, Scope, ZIO, ZLayer, durationInt}
import zio.test.*

import java.time.ZoneId
import java.time.format.DateTimeFormatter

object OneFrameServiceTest extends ZIOSpecDefault {

  def spec: Spec[TestEnvironment & Scope, Any] = suite("OneFrameServiceTest")(

    test("Service uses client to request rate when internal storage is empty") {
      val from = "USD"
      val to = "JPY"
      val value = 100
      for {
        client <- ZIO.service[Client]
        _ <- mockedHttpClient(from, to, value)
        service <- ZIO.service[OneFrameService]
        result <- service.getRate(from, to)
      } yield assertTrue(result == value)
    }.provide(TestClient.layer, OneFrameService.layer, appConfigLayer, storageLayer),

    test("Service returns rate in storage when it has, does not use client") {
      val clientCounter = MutableInt(0)
      val from = "USD"
      val to = "JPY"
      val value = 100
      for {
        client <- ZIO.service[Client]
        _ <- mockedHttpClient(clientCounter)(from, to, value)
        service <- ZIO.service[OneFrameService]
        result1 <- service.getRate(from, to)
        result2 <- service.getRate(from, to)
      } yield assertTrue(result1 == result2 && clientCounter.value == 1)
    }.provide(TestClient.layer, OneFrameService.layer, appConfigLayer, storageLayer),


    test("After requesting rate from client, rate expires after 5 minutes") {
      val clientCounter = MutableInt(0)
      val from = "USD"
      val to = "JPY"
      val value = 100
      for {
        client <- ZIO.service[Client]
        _ <- mockedHttpClient(clientCounter)(from, to, value)
        service <- ZIO.service[OneFrameService]
        result1 <- service.getRate(from, to)
        result2 <- TestClock.adjust(301.seconds) *> service.getRate(from, to)
      } yield assertTrue(result1 == result2 && clientCounter.value == 2)
    }.provide(TestClient.layer, OneFrameService.layer, appConfigLayer, storageLayer),
  )

  private val appConfig: AppConfig = AppConfig("https://ofi", "rates", 300)
  private val appConfigLayer = ZLayer.fromFunction(() => AppConfig("https://ofi", "rates", 300))
  private val storageLayer = ZLayer.fromFunction(() => CurrencyRateStorage(appConfig))

  private def mockedHttpClient(counter: MutableInt)(currencyFrom: String, currencyTo: String, value: Int): ZIO[TestClient, Nothing, Unit] =
    for {
      clockInstant <- Clock.instant
      time = clockInstant.atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_INSTANT)
      _ <- TestClient.addRoutes({
        Routes(
          Method.GET / appConfig.ratesEndpoint -> handler { (request: Request) =>
            counter.inc()
            val from = request.queryParam("from").get
            val to = request.queryParam("to").get
            Response.text(
              s"""{"from":"$currencyFrom","to":"$currencyTo","price":$value,"timestamp":"$time"}"""
            )
          },
        )
      })
    } yield ()

  private def mockedHttpClient(currencyFrom: String, currencyTo: String, value: Int): ZIO[TestClient, Nothing, Unit] =
    mockedHttpClient(MutableInt(0))(currencyFrom, currencyTo, value)

  class MutableInt(var value: Int) {
    def inc(): Unit = {value+=1}
  }
}
