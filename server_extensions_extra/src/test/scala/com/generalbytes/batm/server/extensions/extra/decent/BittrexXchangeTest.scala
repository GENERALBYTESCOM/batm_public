package com.generalbytes.batm.server.extensions.extra.decent

import cats.implicits._
import com.generalbytes.batm.common.Alias.Task
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.{Currency, Exchange, LoggingSupport, TradeOrder}
import com.generalbytes.batm.server.extensions.extra.decent.exchanges.btrx.DefaultBittrexXChangeWrapper
import com.generalbytes.batm.server.extensions.extra.decent.extension.LoginInfo
import org.scalatest.{FlatSpec, Matchers}

class BittrexXchangeTest extends FlatSpec with Matchers with TestLoggingSupport {
  val zero: BigDecimal = BigDecimal.valueOf(0.0)

  protected def createExchange: Exchange[Task] = {
    val credentials = LoginInfo("9c1b049844d84271b7a606311953b758", "1607470db4dc4fddb56eb58df156f672")
    val exchange = new DefaultBittrexXChangeWrapper[Task](credentials).withRetries(2)
    exchange
  }

  it should "not fail when processing BUY order" in {
    val exchange = createExchange

    val amount = BigDecimal(21L)
    val order = TradeOrder.buy(Currency.Decent, Currency.Bitcoin, amount)
    val result = exchange.fulfillOrder(order).attempt.unsafeRunSync()
    result.left.foreach(println)
    result.foreach(println)
    result.getOrThrow should not be empty
  }

  it should "not fail when processing sell order DCT->BTC" in {
    val exchange = createExchange

    val amount = BigDecimal(120)
    val order = TradeOrder.sell(Currency.Decent, Currency.Bitcoin, amount)
    val result = exchange.fulfillOrder(order)
    val value = result.attempt.unsafeRunSync().log.getOrThrow
    println(value)
    value should not be empty
  }

//  it should "not fail when processing sell order BTC->USD" in {
//    val exchange = createExchange
//
//    val amount = BigDecimal(0.0005)
//    val order = TradeOrder.sell(Currency.Bitcoin, Currency.USDollar, amount)
//    val result = exchange.fulfillOrder(order)
//    val value = result.attempt.unsafeRunSync().log.getOrThrow
//    println(value)
//    value should not be empty
//  }

  it should "not fail when getting balance in BTC" in {
    val exchange = createExchange

    val result = exchange.getBalance(Currency.Bitcoin)
    val value = result.attempt.unsafeRunSync().log.getOrThrow
    println(value)
    value should be >= zero
  }

  it should "not fail when getting balance in DCT" in {
    val exchange = createExchange

    val result = exchange.getBalance(Currency.Decent)
    val value = result.attempt.unsafeRunSync().log.getOrThrow
    println(value)
    value should be >= zero
  }

  it should "not fail when getting balance in USD" in {
    val exchange = createExchange

    val result = exchange.getBalance(Currency.USDollar)
    val value = result.attempt.unsafeRunSync().log.getOrThrow
    println(value)
    value should be >= zero
  }
}
