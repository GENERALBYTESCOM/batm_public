package com.generalbytes.batm.server.extensions.extra.decent

import cats.implicits._
import com.generalbytes.batm.server.extensions.extra.decent.extension.DecentExtension
import com.generalbytes.batm.common.implicits._
import org.scalatest.{FlatSpec, Matchers}
import org.slf4j.{Logger, LoggerFactory}

class DecentWalletRestApiTest extends FlatSpec with Matchers {
  implicit val logger: Logger = LoggerFactory.getLogger("test")
  val zero: BigDecimal = BigDecimal.valueOf(0L)

  it should "return a nice error when sending money to invalid address" in {
    val loginInfo = "dctd:http:admin:admin:207.154.255.239:9696"
    val walletApi = new DecentExtension().createWallet(loginInfo).right.get
    val error = walletApi.issuePayment("N/A", 10).attempt.unsafeRunSync().left.getOrElse(null)
    error should not be null
  }

  it should "return work when sending a small payment" in {
    val loginInfo = "dctd:http:admin:admin:207.154.255.239:9696"
    val walletApi = new DecentExtension().createWallet(loginInfo).right.get
    val validAddress = "ud4842222bc4bba988f3f696929dab106"
    val result = walletApi.issuePayment(validAddress, 0.01).attempt.unsafeRunSync().log
    result.left.foreach(println)
    result.right.get should not be empty
  }

  it should "get balance" in {
    val loginInfo = "dctd:http:admin:admin:207.154.255.239:9696"
    val walletApi = new DecentExtension().createWallet(loginInfo).right.get
    val result = walletApi.getBalance.attempt.unsafeRunSync().log
    result.left.getOrElse(null) should be (null)
    result.right.get should be > zero
  }
}
