package com.generalbytes.batm.common.adapters

import java.util

import cats.{Applicative, Id, ~>}
import com.generalbytes.batm.common.Alias.{Attempt, Interpreter}
import com.generalbytes.batm.common.Currency.Default
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.{Currency, Extension, LoggingSupport}
import com.generalbytes.batm.server.extensions._
import com.generalbytes.batm.server.extensions.watchlist.IWatchList

class ExtensionAdapter[F[_]: Applicative : Interpreter, T <: Currency : Default](ext: Extension[F, T])(implicit val g: Attempt ~> Id)
  extends IExtension with LoggingSupport {

  override def getName: String = ext.name

  override def getSupportedCryptoCurrencies: util.Set[String] = ext.supportedCryptoCurrencies.map(_.name).toJavaSet

  override def createExchange(loginInfo: String): IExchange = g(ext.createExchange(loginInfo))

  override def createPaymentProcessor(s: String): IPaymentProcessor = null

  override def createRateSource(loginInfo: String): IRateSource = g(ext.createRateSource(loginInfo))

  override def createWallet(loginInfo: String): IWallet =
    g {
      ext.createWallet(loginInfo)
        .map(new WalletAdapter(_))
    }

  override def createAddressValidator(cryptoCurrency: String): ICryptoAddressValidator =
    g(ext.createAddressValidator)

  override def createPaperWalletGenerator(s: String): IPaperWalletGenerator = null

  override def getSupportedWatchListsNames: util.Set[String] = Set.empty[String].toJavaSet

  override def getWatchList(s: String): IWatchList = null

  override def init(iExtensionContext: IExtensionContext): Unit = ()

  override def getCryptoCurrencyDefinitions: util.Set[ICryptoCurrencyDefinition] = Set.empty[ICryptoCurrencyDefinition].toJavaSet
}
