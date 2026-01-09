package oneframeinterpreter.oneframe

import zio.Scope
import zio.test.{Spec, TestEnvironment, ZIOSpecDefault}

object OneFrameServiceTest extends ZIOSpecDefault {

  def spec: Spec[TestEnvironment & Scope, Any] = suite("OneFrameServiceTest")(
    test("Service requests a rate from the client when it doesn't have one in storage") {
      assert(false)
    },

    test("Service returns rate in storage when it has, does not use client") {
      assert(false)
    },

    test("After requesting rate from client, rate expires after 5 minutes") {
      assert(false)
    }
  )

}
