package com.generalbytes.batm.server.extensions.extra.decent.extension

import cats.implicits._
import com.generalbytes.batm.common.Alias.{Attempt, Task}
import com.generalbytes.batm.common.Currency.DCT
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.Wallet
import com.generalbytes.batm.common.factories.WalletFactory
import com.generalbytes.batm.server.extensions.extra.decent.wallets.dctd.DecentWalletRestApi
import org.http4s.Uri

trait DecentHotWalletFactory extends WalletFactory[Task, DCT] {
  private val walletLoginData = """dctd:(https?):([A-Za-z0-9]+):([A-Za-z0-9\.]+):([A-Za-z0-9.]+):([0-9]+)""".r

  override def createWallet(loginInfo: String): Attempt[Wallet[Task, DCT]] = loginInfo match {
    case walletLoginData(protocol, user, password, hostname, port) =>
      Uri.fromString(s"$protocol://$hostname:$port")
        .map(uri => new DecentWalletRestApi(uri, DecentWalletRestApi.DecentWalletCredentials(user, password)))
    case _ => err"Login info ($loginInfo) did not match the expected format".asLeft
  }
}
