package com.generalbytes.batm.common

trait RateSource[F[_]] extends ExchangeBase {
  def getExchangeRateForSell(currencyPair: CurrencyPair): F[BigDecimal]
  def getExchangeRateForBuy(currencyPair: CurrencyPair): F[BigDecimal]
}

trait ExchangeBase {
  val cryptoCurrencies: Set[CryptoCurrency]
  val fiatCurrencies: Set[FiatCurrency]
  val preferredFiat: FiatCurrency
}
