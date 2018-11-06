package com.generalbytes.batm.server.extensions.extra.decent.extension

import com.generalbytes.batm.common.Alias.Task
import com.generalbytes.batm.common.factories.DummyAddressValidatorFactory
import com.generalbytes.batm.common.{CryptoCurrency, Currency, Extension}

class DecentExtension extends Extension[Task, Currency.DCT]
  with CompositeRateSourceFactory
  with BittrexExchangeFactory
  with DecentHotWalletFactory
  with DummyAddressValidatorFactory {

  override val name: String = "DCT Extension"
  override val supportedCryptoCurrencies: Set[CryptoCurrency] = Set(Currency.Decent, Currency.Bitcoin)
}


