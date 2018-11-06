package com.generalbytes.batm.common.adapters

import cats.effect.Sync
import cats.implicits._
import com.generalbytes.batm.common.Alias.ExchangeRate
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common._
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced

class ReverseRateSourceAdapter[F[_]: Sync](rs: IRateSourceAdvanced) extends RateSource[F] {
  override def getExchangeRateForSell(currencyPair: CurrencyPair): F[ExchangeRate] =
    Sync[F].delay {
      rs.getExchangeRateForSell(currencyPair.counter.name, currencyPair.base.name)
    }.map(BigDecimal.apply)


  override def getExchangeRateForBuy(currencyPair: CurrencyPair): F[ExchangeRate] =
    Sync[F].delay {
      rs.getExchangeRateForBuy(currencyPair.counter.name, currencyPair.base.name)
    }.map(BigDecimal.apply)

  override val cryptoCurrencies: Set[CryptoCurrency] = Currency.cryptos.filter(c => rs.getCryptoCurrencies.contains(c.name))
  override val fiatCurrencies: Set[FiatCurrency] = Currency.fiats.filter(f => rs.getFiatCurrencies.contains(f.name))
  override val preferredFiat: FiatCurrency = Currency.withName(rs.getPreferredFiatCurrency).cast[FiatCurrency].right.get
}
