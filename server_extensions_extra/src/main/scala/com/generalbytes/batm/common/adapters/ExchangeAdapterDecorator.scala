package com.generalbytes.batm.common.adapters

import com.generalbytes.batm.common.Alias.{Address, Amount, Identifier}
import com.generalbytes.batm.common._

abstract class ExchangeAdapterDecorator[F[_]](exchange: Exchange[F]) extends Exchange[F] {
  override def getBalance(currency: Currency): F[Amount] = exchange.getBalance(currency)

  override def getAddress(currency: Currency): F[Address] = exchange.getAddress(currency)

  override def withdrawFunds(currency: Currency, amount: Amount, destination: Address): F[Identifier] =
    exchange.withdrawFunds(currency, amount, destination)

  override val cryptoCurrencies: Set[CryptoCurrency] = exchange.cryptoCurrencies
  override val fiatCurrencies: Set[FiatCurrency] = exchange.fiatCurrencies
  override val preferredFiat: FiatCurrency = exchange.preferredFiat
}
