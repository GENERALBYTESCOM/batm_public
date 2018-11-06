package com.generalbytes.batm.common.factories

import cats.implicits._
import com.generalbytes.batm.common.Alias.{Address, Attempt}
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator

trait DummyAddressValidatorFactory extends AddressValidatorFactory {
  private class DummyAddressValidator extends ICryptoAddressValidator {
    override def isAddressValid(address: Address): Boolean = true

    override def mustBeBase58Address(): Boolean = false

    override def isPaperWalletSupported: Boolean = false
  }

  def createAddressValidator: Attempt[ICryptoAddressValidator] = new DummyAddressValidator().asRight
}
