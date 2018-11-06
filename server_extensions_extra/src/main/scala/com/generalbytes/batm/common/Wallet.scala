package com.generalbytes.batm.common

import Alias.{Address, Amount, Identifier}

import scala.language.higherKinds

trait Wallet[F[_], T <: Currency] {
  def getAddress: F[Address]
  def issuePayment(recipientAddress: Address, amount: Amount, description: String = ""): F[Identifier]
  def getBalance: F[Amount]
}
