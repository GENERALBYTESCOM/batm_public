package com.generalbytes.batm.common.factories

import cats.implicits._
import com.generalbytes.batm.common.Alias.Attempt
import com.generalbytes.batm.server.extensions.IExchangeAdvanced
import com.generalbytes.batm.server.extensions.extra.decent.exchanges.dummy.DummyExchange

trait DummyExchangeFactory extends ExchangeFactory {
  def createExchange(loginInfo: String): Attempt[IExchangeAdvanced] = new DummyExchange().asRight
}
