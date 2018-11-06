package com.generalbytes.batm.server.extensions.extra.decent

import com.generalbytes.batm.common.Alias.{Amount, Task}
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.{Currency, CurrencyPair, Exchange, TradeOrder}
import com.generalbytes.batm.server.extensions.extra.decent.exchanges.btrx.{CounterReplacingXChangeWrapper, DefaultBittrexXChangeWrapper}
import com.generalbytes.batm.server.extensions.extra.decent.extension.LoginInfo
import org.scalactic.source.Position
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

class CounterReplacementBittrexXChangeTest extends FlatSpec with Matchers with TestLoggingSupport with BeforeAndAfter {
  override def before(fun: => Any)(implicit pos: Position): Unit = createExchange()

  val zero: BigDecimal = BigDecimal.valueOf(0.0)
  private var underlying: Exchange[Task] = _
  private var exchange: Exchange[Task] = _

  private def createExchange(): Unit = {
    val credentials = LoginInfo("9c1b049844d84271b7a606311953b758", "1607470db4dc4fddb56eb58df156f672")
    underlying = new DefaultBittrexXChangeWrapper[Task](credentials)
    exchange = new CounterReplacingXChangeWrapper(underlying,
      List(CurrencyPair(Currency.USDollar, Currency.Bitcoin), CurrencyPair(Currency.Euro, Currency.Bitcoin)),
      List(Currency.USDollar))
  }

  it should "get balance in replace currency" in {
    val usdBalance = exchange.getBalance(Currency.USDollar).unsafeRunSync()
    val btcBalance = underlying.getBalance(Currency.Bitcoin).unsafeRunSync()

    println(btcBalance)
    println(usdBalance)
    btcBalance should not equal usdBalance
  }

  it should "get balance in replace currency eur" in {
    val usdBalance = exchange.getBalance(Currency.Euro).unsafeRunSync()
    val btcBalance = underlying.getBalance(Currency.Bitcoin).unsafeRunSync()

    println(btcBalance)
    println(usdBalance)
    btcBalance should not equal usdBalance
  }

//  it should "not fail when processing BUY order USD->DCT" in {
//
//    val amount = BigDecimal(90)
//    val order = TradeOrder.buy(Currency.Decent, Currency.Euro, amount)
//    val result = exchange.fulfillOrder(order).attempt.unsafeRunSync()
//    result.left.foreach(println)
//    result.foreach(println)
//    result.getOrThrow should not be empty
//  }
//
//  it should "not fail when processing sell order (DCT -> BTC)" in {
//    val amount = BigDecimal(90)
//    val order = TradeOrder.sell(Currency.Decent, Currency.Euro, amount)
//    val result = exchange.fulfillOrder(order).attempt.unsafeRunSync()
//    result.left.foreach(println)
//    result.foreach(println)
//    result.getOrThrow should not be empty
//  }
}
