package com.generalbytes.batm.common.ratesources

import cats.Applicative
import com.generalbytes.batm.common.Alias.{ApplicativeErr, ExchangeRate}
import com.generalbytes.batm.common._
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.Util._
import shapeless.syntax.std.product._

class SingleFixedPriceRateSource[F[_] : Applicative : ApplicativeErr](currencyPair: CurrencyPairF2C, rate: ExchangeRate) extends RateSource[F] {
  override val cryptoCurrencies: Set[CryptoCurrency] = Set(currencyPair.base)
  override val fiatCurrencies: Set[FiatCurrency] = Set(currencyPair.counter)
  override val preferredFiat: FiatCurrency = currencyPair.counter

  override def getExchangeRateForSell(currencyPair: CurrencyPair): F[ExchangeRate] =
    if (currencyPair.toTuple == this.currencyPair.toTuple) {
      Applicative[F].pure(rate)
    } else raise[F](err"Unsupported currency pair $currencyPair")

  override def getExchangeRateForBuy(currencyPair: CurrencyPair): F[BigDecimal] =
    if (currencyPair.toTuple == this.currencyPair.toTuple) {
      Applicative[F].pure(rate)
    } else raise[F](err"Unsupported currency pair $currencyPair")
}
