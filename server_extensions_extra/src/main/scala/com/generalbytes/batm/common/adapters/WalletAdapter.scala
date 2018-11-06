package com.generalbytes.batm.common.adapters

import java.math.BigDecimal
import java.util

import cats._
import cats.implicits._
import com.generalbytes.batm.common.Adapters.{Address, Amount, TransactionId}
import com.generalbytes.batm.common.Alias.Interpreter
import com.generalbytes.batm.common.{Adapters, Currency, Wallet}
import com.generalbytes.batm.server.extensions.IWallet

import scala.collection.JavaConverters._
import scala.collection.mutable

class WalletAdapter[F[_] : Applicative : Interpreter, T <: Currency : Currency.Default](wallet: Wallet[F, T]) extends IWallet {

  override def sendCoins(address: Address, amount: Amount, currency: String, desc: String): TransactionId =
    Interpreter[F].apply(wallet.issuePayment(address, amount.toBigInteger.longValue, desc))

  override def getCryptoAddress(cryptoCurrency: Adapters.CryptoCurrency): Address =
    Interpreter[F].apply(wallet.getAddress)

  override def getCryptoBalance(cryptoCurrency: Adapters.CryptoCurrency): BigDecimal =
    Interpreter[F].apply(wallet.getBalance.map(_.bigDecimal))

  override def getCryptoCurrencies: util.Set[Adapters.CryptoCurrency] = mutable.Set(Currency[T].name).asJava

  override def getPreferredCryptoCurrency: Adapters.CryptoCurrency = Currency[T].name
}
