package com.generalbytes.batm.server.extensions.extra.decent.sources.fixed

import cats.Applicative
import com.generalbytes.batm.common.Alias.ApplicativeErr
import com.generalbytes.batm.common.ratesources.SingleFixedPriceRateSource
import com.generalbytes.batm.common.{Currency, CurrencyPairF2C}

class EurToDctFixedRateSource[F[_] : Applicative : ApplicativeErr](rate: BigDecimal)
  extends SingleFixedPriceRateSource[F](CurrencyPairF2C(Currency.Euro, Currency.Decent), rate)

object EurToDctFixedRateSource {
  val defaultRate = BigDecimal(5.1)
}
