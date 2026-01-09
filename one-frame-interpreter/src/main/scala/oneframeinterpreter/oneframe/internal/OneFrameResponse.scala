package oneframeinterpreter.oneframe.internal

import zio.json.JsonDecoder

case class OneFrameResponse(from: String, to: String, price: Int, timestamp: String) derives JsonDecoder

