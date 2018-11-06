package com.generalbytes.batm.common

import com.generalbytes.batm.common.Alias.Amount

final case class CryptoAmount[T <: Currency](amount: Amount)

sealed trait TradeOrder {
  val currencyPair: CurrencyPair
  val amount: CryptoAmount[Currency]

  def copy(currencyPair: CurrencyPair = this.currencyPair, amount: CryptoAmount[Currency] = this.amount): TradeOrder = this match {
    case PurchaseOrder(_, _) => PurchaseOrder(currencyPair, amount)
    case SaleOrder(_, _) => SaleOrder(currencyPair, amount)
  }

  def inverse: TradeOrder = this match {
    case PurchaseOrder(_, _) => SaleOrder(currencyPair, amount)
    case SaleOrder(_, _) => PurchaseOrder(currencyPair, amount)
  }
}

final case class PurchaseOrder(currencyPair: CurrencyPair, amount: CryptoAmount[Currency]) extends TradeOrder
final case class SaleOrder(currencyPair: CurrencyPair, amount: CryptoAmount[Currency]) extends TradeOrder

object TradeOrder {
  def buy(cryptoCurrency: CryptoCurrency, counter: Currency, amount: Amount): TradeOrder =
    PurchaseOrder(CurrencyPair(counter, cryptoCurrency), CryptoAmount[Currency](amount))

  def sell(cryptoCurrency: CryptoCurrency, counter: Currency, amount: Amount): TradeOrder =
    SaleOrder(CurrencyPair(counter, cryptoCurrency), CryptoAmount[Currency](amount))
}
