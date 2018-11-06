package com.generalbytes.batm.server.extensions.extra.decent.extension

import com.generalbytes.batm.common.Alias.Attempt
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.factories.RateSourceFactory
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced

trait BittrexExchangeAndRateSourceFactory extends BittrexExchangeFactory with RateSourceFactory {
  def createRateSource(loginInfo: String): Attempt[IRateSourceAdvanced] = createExchange(loginInfo).cast[IRateSourceAdvanced]
}
