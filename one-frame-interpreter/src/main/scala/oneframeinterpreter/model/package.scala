package oneframeinterpreter

import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.string.MatchesRegex

package object model {
  type CurrencyName = String Refined MatchesRegex["[A-Z]{3}"]

  object CurrencyName extends RefinedTypeOps[CurrencyName, String] {
    override def from(t: String): Either[String, CurrencyName] =
      super.from(t)
  }
}
