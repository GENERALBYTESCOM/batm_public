package com.generalbytes.batm.server.extensions.extra.decent.exchanges.dummy

import java.util

import com.generalbytes.batm.server.extensions.{IExchangeAdvanced, ITask}

class DummyExchange extends IExchangeAdvanced {
  override def createPurchaseCoinsTask(bigDecimal: java.math.BigDecimal, s: String, s1: String, s2: String): ITask = ???

  override def createSellCoinsTask(bigDecimal: java.math.BigDecimal, s: String, s1: String, s2: String): ITask = ???

  override def getCryptoCurrencies: util.Set[String] = ???

  override def getFiatCurrencies: util.Set[String] = ???

  override def getPreferredFiatCurrency: String = ???

  override def getCryptoBalance(s: String): java.math.BigDecimal = ???

  override def getFiatBalance(s: String): java.math.BigDecimal = ???

  override def purchaseCoins(bigDecimal: java.math.BigDecimal, s: String, s1: String, s2: String): String = ???

  override def sellCoins(bigDecimal: java.math.BigDecimal, s: String, s1: String, s2: String): String = ???

  override def sendCoins(s: String, bigDecimal: java.math.BigDecimal, s1: String, s2: String): String = ???

  override def getDepositAddress(s: String): String = ???
}
