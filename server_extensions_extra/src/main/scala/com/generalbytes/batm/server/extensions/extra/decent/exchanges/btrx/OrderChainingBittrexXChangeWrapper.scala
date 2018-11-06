package com.generalbytes.batm.server.extensions.extra.decent.exchanges.btrx

import cats.Monad
import cats.effect.{ConcurrentEffect, Sync}
import cats.implicits._
import com.generalbytes.batm.common.Alias._
import com.generalbytes.batm.common.Util._
import com.generalbytes.batm.common._
import com.generalbytes.batm.common.adapters.ExchangeAdapterDecorator
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.server.extensions.extra.decent.exchanges.btrx.DefaultBittrexXChangeWrapper.ErrorDecorator
import com.generalbytes.batm.server.extensions.extra.decent.sources.btrx.FallbackBittrexTicker
import org.knowm.xchange.dto.Order.OrderType
import shapeless._

class OrderChainingBittrexXChangeWrapper[F[_]: Sync : ApplicativeErr : Monad : ConcurrentEffect]
  (exchange: Exchange[F], midCurrency: Currency)
  extends ExchangeAdapterDecorator[F](exchange) with LoggingSupport {
  import XChangeConversions._

  private val amountLens = lens[TradeOrder].amount.amount
  private val currencyPairLens = lens[TradeOrder].currencyPair
  private val currencyPairAndAmountLens = currencyPairLens ~ amountLens

  override def fulfillOrder(order: TradeOrder): F[Identifier] = {
    if (order.currencyPair.counter === midCurrency || order.currencyPair.base === midCurrency) exchange.fulfillOrder(order)
    else {
      val midCurrencyAmount = getAmountInMidCurrency(order)

      val result = for {
        amount <- midCurrencyAmount
        fstOrder = createFirstSubOrder(order, amount)
        _ <- log(fstOrder)
        _ <- exchange.fulfillOrder(fstOrder)
        sndOrderTemp = createSecondSubOrder(order, amount)
        revisedAmount <- getInverseAmountInCurrency(CurrencyPair(midCurrency, order.currencyPair.base), getOrderType(order), amount)   // TODO: refactor this
        _ <- log(revisedAmount, "RevisedAmount")
        sndOrder = createSecondSubOrder(sndOrderTemp, revisedAmount)
        _ <- log(sndOrder)
        txId <- exchange.fulfillOrder(sndOrder)
      } yield txId

      result.handleErrorWith {
        case e @ ErrorDecorator(_, CurrencyPair(_, mc)) if mc === midCurrency =>
          // failed on second currency
          val undoTransaction = createUndoOrder(order, midCurrencyAmount)
          undoTransaction.flatMap(_ => raise[F](e))
        case e => raise[F](e)
      }
    }
  }

  private def getInverseAmountInCurrency(currencyPair: CurrencyPair, orderType: OrderType, counterAmount: Amount): F[Amount] =
    getAmountInCurrency(currencyPair, orderType.inverse, counterAmount, _ / _)

  private def getAmountInCurrency(currencyPair: CurrencyPair, orderType: OrderType, counterAmount: Amount, f: (Amount, ExchangeRate) => Amount): F[Amount] = {
    val ticker = new FallbackBittrexTicker[F](currencyPair)
    val selector = getRateSelector(orderType)
    for {
      rate <- ticker.currentRates
      amount = f(counterAmount, selector(rate))
      _ <- log(amount, "amount")
    } yield amount
  }

  private def createUndoOrder(order: TradeOrder, midCurrencyAmount: F[Amount]): F[Identifier] = {
    for {
      amount <- midCurrencyAmount
      undoOrder = createFirstSubOrder(order, amount).inverse
      _ <- log(undoOrder.toString, "UndoOrder")
      undoTxId <- exchange.fulfillOrder(undoOrder)
      _ <- log(undoTxId, "UndoTransactionId")
    } yield undoTxId
  }

  private def createFirstSubOrder(order: TradeOrder, amount: Amount): TradeOrder = {
    val firstCP = CurrencyPair(order.currencyPair.counter, midCurrency)
    currencyPairAndAmountLens.set(order)(firstCP, amount)
  }

  private def createSecondSubOrder(order: TradeOrder, amount: Amount): TradeOrder = {
    val secondCP = CurrencyPair(midCurrency, order.currencyPair.base)
    currencyPairAndAmountLens.set(order)(secondCP, amount)
  }

  private def getAmountInMidCurrency(order: TradeOrder): F[Amount] = {
    getAmountInCurrency(CurrencyPair(midCurrency, order.currencyPair.base), getOrderType(order), order.amount.amount, _ * _)
  }
}
