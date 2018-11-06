package com.generalbytes.batm.server.extensions.extra.decent

import cats.implicits._
import com.generalbytes.batm.common.Alias.Task
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.{Currency, CurrencyPair, LoggingSupport}
import com.generalbytes.batm.server.extensions.extra.decent.sources.btrx.BittrexWrapperRateSource
import org.scalatest.{FlatSpec, Matchers}

class DynamicBittrexRateSourceTest extends FlatSpec with Matchers with TestLoggingSupport {
  val zero: BigDecimal = BigDecimal.valueOf(0L)

  it should "get the buy rate source" in {
    val rs = new BittrexWrapperRateSource[Task](Currency.USDollar :: Currency.Bitcoin :: Nil)
    val result = rs.getExchangeRateForBuy(CurrencyPair(Currency.Euro, Currency.Decent)).unsafeRunSync()
    println(result)
    result should be > zero
  }

  it should "get the buy rate source orig" in {
    val rs = new BittrexWrapperRateSource[Task](Currency.USDollar :: Currency.Bitcoin :: Nil)
    val result = rs.getExchangeRateForBuy(CurrencyPair(Currency.USDollar, Currency.Bitcoin)).unsafeRunSync()
    println(result)
    result should be > zero
  }

  it should "get the buy rate source for diff currency" in {
    val rs = new BittrexWrapperRateSource[Task](Currency.USDollar :: Currency.Bitcoin :: Nil)
    val result = rs.getExchangeRateForBuy(CurrencyPair(Currency.Euro, Currency.Bitcoin)).unsafeRunSync()
    println(result)
    result should be > zero
  }

  it should "get the buy rate source without replacement" in {
    val rs = new BittrexWrapperRateSource[Task](Currency.Bitcoin :: Nil)
    val result = rs.getExchangeRateForBuy(CurrencyPair(Currency.Bitcoin, Currency.Decent)).unsafeRunSync()
    println(result)
    result should be > zero
  }

  it should "get the sell rate source" in {
    val rs = new BittrexWrapperRateSource[Task](Currency.USDollar :: Currency.Bitcoin :: Nil)
    val result = rs.getExchangeRateForSell(CurrencyPair(Currency.Euro, Currency.Decent)).unsafeRunSync()
    println(result)
    result should be > zero
  }

  it should "get the sell rate source for diff currency" in {
    val rs = new BittrexWrapperRateSource[Task](Currency.USDollar :: Currency.Bitcoin :: Nil)
    val result = rs.getExchangeRateForSell(CurrencyPair(Currency.Euro, Currency.Bitcoin)).unsafeRunSync()
    println(result)
    result should be > zero
  }
}
