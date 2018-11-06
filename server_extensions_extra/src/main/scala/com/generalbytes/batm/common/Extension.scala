package com.generalbytes.batm.common

import com.generalbytes.batm.common.factories.{AddressValidatorFactory, ExchangeFactory, RateSourceFactory, WalletFactory}

trait Extension[F[_], T <: Currency]
  extends ExchangeFactory
    with RateSourceFactory
    with WalletFactory[F, T]
    with AddressValidatorFactory {
  val name: String
  val supportedCryptoCurrencies: Set[CryptoCurrency]
}
