package com.generalbytes.batm.server.extensions.extra.decent.extension

import cats.implicits._
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.Alias.{Attempt, Task}
import com.generalbytes.batm.common.Currency
import com.generalbytes.batm.common.adapters.RateSourceAdapter
import com.generalbytes.batm.common.factories.RateSourceFactory
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced
import com.generalbytes.batm.server.extensions.extra.decent.sources.btrx.BittrexWrapperRateSource

trait BittrexWrapperRateSourceFactory extends RateSourceFactory {
  def createRateSource(loginInfo: String): Attempt[IRateSourceAdvanced] =
    new RateSourceAdapter[Task](new BittrexWrapperRateSource(List(Currency.USDollar, Currency.Bitcoin))).asRight
}
