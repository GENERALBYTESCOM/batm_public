package com.generalbytes.batm.server.extensions.extra.decent.extension

import cats.implicits._
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.Alias.{Attempt, Task}
import com.generalbytes.batm.common.Currency
import com.generalbytes.batm.common.adapters.RateSourceAdapter
import com.generalbytes.batm.common.factories.RateSourceFactory
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced
import com.generalbytes.batm.server.extensions.extra.decent.sources.btrx.BittrexWrapperRateSource
import org.slf4j.{Logger, LoggerFactory}

trait CompositeRateSourceFactory extends RateSourceFactory with FixedPriceRateSourceFactory {
  implicit val logger: Logger = LoggerFactory.getLogger("CompositeRateSourceFactory")

  def create(loginInfo: String): Attempt[IRateSourceAdvanced] = loginInfo match {
    case _ =>
      logger.debug(s"Login info: $loginInfo")
      new RateSourceAdapter[Task](new BittrexWrapperRateSource(List(Currency.USDollar, Currency.Bitcoin))).asRight
  }

  override def createRateSource(loginInfo: String): Attempt[IRateSourceAdvanced] =
    super[FixedPriceRateSourceFactory].createRateSource(loginInfo) orElse create(loginInfo)
}
