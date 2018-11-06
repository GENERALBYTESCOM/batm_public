package com.generalbytes.batm.server.extensions.extra.decent.exchanges.btrx

import com.generalbytes.batm.common.Alias.ExchangeRate
import com.generalbytes.batm.common._
import com.generalbytes.batm.server.extensions.extra.decent.sources.btrx.BittrexTick
import org.knowm.xchange.currency
import org.knowm.xchange.dto.Order.OrderType

object XChangeConversions {
   implicit class CurrencyConv(c: Currency) {
     def convert: currency.Currency = currency.Currency.getInstance(c.name)
   }

  implicit class CurrencyPairConv(cp: CurrencyPair) {
    def convert: currency.CurrencyPair = new currency.CurrencyPair(cp.base.name, cp.counter.name)
  }

  implicit class OrderTypeOps(orderType: OrderType) {
    def inverse: OrderType = orderType match {
      case OrderType.ASK => OrderType.BID
      case OrderType.BID => OrderType.ASK
    }
  }

  type RateSelector = BittrexTick => ExchangeRate
  def getRateSelector(orderType: OrderType): RateSelector = orderType match {
    case OrderType.ASK => _.ask
    case OrderType.BID => _.bid
  }

  def getOrderType(order: TradeOrder): OrderType = order match {
    case _:PurchaseOrder => OrderType.BID
    case _:SaleOrder => OrderType.ASK
  }
}
