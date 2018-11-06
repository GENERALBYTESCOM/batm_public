package com.generalbytes.batm.common

import com.generalbytes.batm.common.Alias.{Address, Amount, Identifier}

trait Exchange[F[_]] extends ExchangeBase {
  def getBalance(currency: Currency): F[Amount]
  def getAddress(currency: Currency): F[Address]
  def fulfillOrder(order: TradeOrder): F[Identifier]
  def withdrawFunds(currency: Currency, amount: Amount, destination: Address): F[Identifier]
}
