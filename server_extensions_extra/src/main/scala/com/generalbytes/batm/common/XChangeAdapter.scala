package com.generalbytes.batm.common

import java.util
import java.math.BigDecimal
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.XChangeExchange
import com.generalbytes.batm.server.extensions.{IExchangeAdvanced, IRateSourceAdvanced, ITask}

class XChangeAdapter[T <: XChangeExchange](xch: T) extends IExchangeAdvanced with IRateSourceAdvanced {
  override def createPurchaseCoinsTask(amount: BigDecimal, cryptoCurrency: String, fiatCurrency: String, description: String): ITask =
    xch.createPurchaseCoinsTask(amount,cryptoCurrency, fiatCurrency, description)

  override def createSellCoinsTask(amount: BigDecimal, cryptoCurrency: String, fiatCurrency: String, description: String): ITask =
    xch.createSellCoinsTask(amount, cryptoCurrency, fiatCurrency, description)

  override def getExchangeRateForBuy(cryptoCurrency: String, fiatCurrency: String): BigDecimal =
    xch.getExchangeRateForBuy(cryptoCurrency, fiatCurrency)

  override def getExchangeRateForSell(cryptoCurrency: String, fiatCurrency: String): BigDecimal =
    xch.getExchangeRateForSell(cryptoCurrency, fiatCurrency)

  override def calculateBuyPrice(cryptoCurrency: String, fiatCurrency: String, amount: BigDecimal): BigDecimal =
    xch.calculateBuyPrice(cryptoCurrency, fiatCurrency, amount)

  override def calculateSellPrice(cryptoCurrency: String, fiatCurrency: String, amount: BigDecimal): BigDecimal =
    xch.calculateSellPrice(cryptoCurrency, fiatCurrency, amount)

  override def getCryptoBalance(cryptoCurrency: String): BigDecimal =
    xch.getCryptoBalance(cryptoCurrency)

  override def getFiatBalance(fiatCurrency: String): BigDecimal =
    xch.getFiatBalance(fiatCurrency)

  override def purchaseCoins(amount: BigDecimal, cryptoCurrency: String, fiatCurrency: String, description: String): String =
    xch.purchaseCoins(amount, cryptoCurrency, fiatCurrency, description)

  override def sellCoins(amount: BigDecimal, cryptoCurrency: String, fiatCurrency: String, description: String): String =
    xch.sellCoins(amount, cryptoCurrency, fiatCurrency, description)

  override def sendCoins(cryptoCurrency: String, amount: BigDecimal, fiatCurrency: String, description: String): String =
    xch.sendCoins(cryptoCurrency, amount, fiatCurrency, description)

  override def getDepositAddress(cryptoCurrency: String): String = xch.getDepositAddress(cryptoCurrency)

  override def getCryptoCurrencies: util.Set[String] = xch.getCryptoCurrencies

  override def getFiatCurrencies: util.Set[String] = xch.getFiatCurrencies

  override def getExchangeRateLast(cryptoCurrency: String, fiatCurrency: String): BigDecimal =
    xch.getExchangeRateLast(cryptoCurrency, fiatCurrency)

  override def getPreferredFiatCurrency: String = xch.getPreferredFiatCurrency
}
