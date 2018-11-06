package com.generalbytes.batm.server.extensions.extra.decent.extension

import cats.implicits._
import com.generalbytes.batm.common.Alias.{Attempt, Task}
import com.generalbytes.batm.common.Util._
import com.generalbytes.batm.common.adapters.RateSourceAdapter
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.factories.RateSourceFactory
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced
import com.generalbytes.batm.server.extensions.extra.decent.sources.fixed.EurToDctFixedRateSource

import scala.util.Try

trait FixedPriceRateSourceFactory extends RateSourceFactory {
  private val rateSourceLoginData = """fixed:([0-9\.]+)""".r

  private def fixedPriceFromRate(rate: BigDecimal): IRateSourceAdvanced =
    new RateSourceAdapter[Task](
      new EurToDctFixedRateSource(rate)
    )

  def createRateSource(loginInfo: String): Attempt[IRateSourceAdvanced] = loginInfo match {
    case rateSourceLoginData(rate) => Either.fromTry(Try(BigDecimal(rate))).map(fixedPriceFromRate)
    case _ => err"Could not create exchange from the parameters: $loginInfo".asLeft[IRateSourceAdvanced]
  }
}
