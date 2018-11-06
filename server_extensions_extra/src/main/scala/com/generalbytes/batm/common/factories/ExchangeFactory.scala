package com.generalbytes.batm.common.factories

import com.generalbytes.batm.common.Alias.Attempt
import com.generalbytes.batm.server.extensions.IExchange

trait ExchangeFactory {
  def createExchange(loginInfo: String): Attempt[IExchange]
}
