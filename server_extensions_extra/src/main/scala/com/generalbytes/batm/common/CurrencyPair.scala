package com.generalbytes.batm.common

import com.generalbytes.batm.common.Alias.Attempt
import Util._

case class CurrencyPair(counter: Currency, base: Currency) {
  def flip: CurrencyPair = CurrencyPair(base, counter)

  override def toString: String = s"${counter.name}-${base.name}"
}
case class CurrencyPairF2C(counter: FiatCurrency, base: CryptoCurrency)

object CurrencyPair {
  def fromNames(from: String, to: String): Attempt[CurrencyPair] = for {
    f <- Currency.withName(from)
    t <- Currency.withName(to)
  } yield CurrencyPair(f, t)
}
