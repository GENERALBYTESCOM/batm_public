package com.generalbytes.batm.server.extensions.extra.decent.exchanges.btrx

import cats._
import cats.syntax.semigroup._
import cats.syntax.apply._
import cats.effect.{ConcurrentEffect, Sync}
import com.generalbytes.batm.common.Alias.{Amount, ApplicativeErr, Identifier}
import com.generalbytes.batm.common._
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.adapters.ExchangeAdapterDecorator
import com.generalbytes.batm.server.extensions.extra.decent.sources.btrx.BittrexWrapperRateSource
import shapeless.syntax.std.product._
import monocle.Lens
import monocle.macros.GenLens

class CounterReplacingXChangeWrapper[F[_]: Sync : ApplicativeErr : Monad : ConcurrentEffect]
  (exchange: Exchange[F], replacements: Seq[CurrencyPair], intermediate: List[Currency])
  extends ExchangeAdapterDecorator[F](exchange) with LoggingSupport {

  private val rateSource = new BittrexWrapperRateSource[F](intermediate)
  private val replacementMap = replacements.map(_.productElements.tupled).toMap
  private val counterLens: Lens[TradeOrder, Currency] =
    Lens[TradeOrder, CurrencyPair](_.currencyPair)(cp => _.copy(cp)) composeLens
      GenLens[CurrencyPair](_.counter)

  override def fulfillOrder(order: TradeOrder): F[Identifier] = {
    val replacementCurrency = replacementMap.get(counterLens.get(order))
    val newOrder = replacementCurrency.map(c => counterLens.set(c)(order)).getOrElse(order)
    exchange.fulfillOrder(newOrder)
  }

  override def getBalance(currency: Currency): F[Amount] ={
    val replacementCurrency = replacementMap.getOrElse(currency, currency)
    val balance = exchange.getBalance(replacementCurrency)
    val exchangeRate = rateSource.getExchangeRateForSell(CurrencyPair(currency, replacementCurrency))
    balance.map2(exchangeRate)(_ * _)
  }
}
