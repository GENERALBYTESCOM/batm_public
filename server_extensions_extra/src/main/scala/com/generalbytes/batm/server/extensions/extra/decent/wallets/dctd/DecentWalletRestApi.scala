package com.generalbytes.batm.server.extensions.extra.decent.wallets.dctd

import cats.implicits._
import com.generalbytes.batm.common.Alias._
import com.generalbytes.batm.common.Currency.DCT
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.{ClientFactory, LoggingSupport, Wallet}
import io.circe.generic.semiauto._
import io.circe.syntax._
import io.circe.{Decoder, ObjectEncoder}
import org.http4s._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._

class DecentWalletRestApi(url: Uri, credentials: DecentWalletRestApi.DecentWalletCredentials) extends Wallet[Task, DCT] with ClientFactory[Task] with LoggingSupport {
  import DecentWalletRestApi._

  override def issuePayment(recipientAddress: Address, amount: Amount, description: String = ""): Task[Identifier] = {
    val purchaseReq = PurchaseRequest(credentials.username, credentials.password, amount, recipientAddress)
    logger.debug(purchaseReq.toString)

    val request = POST (
      url / "buy",
      purchaseReq.asJson
    )

    client
      .flatMap(_.expectOr[TransactionInfo](request)(_.as[ErrorResponse]))
      .map(_.txid)
  }

  override def getBalance: Task[Amount] = {
    val request = GET(url / "balance")
    logger.debug(request.toString)

    client
      .flatMap(_.expectOr[BalanceResponse](request)(_.as[ErrorResponse]))
      .map(_.balance)
  }

  override def getAddress: Task[Address] = Task.raiseError(err"Not implemented")
}

object DecentWalletRestApi {
  case class DecentWalletCredentials(username: String, password: String)

  case class PurchaseRequest(username: String, password: String, amount: Amount, address: String)

  case class TransactionInfo(amountLeftInWallet: Amount, txid: Identifier)

  case class PurchaseResponse(b: PurchaseRequest, r: TransactionInfo)

  case class BalanceResponse(balance: Amount)

  case class ErrorResponse(code: Int, message: String) extends Throwable(message) {
    override def toString: String = s"code: $code, message: $message"
  }

  implicit val errorDecoder: Decoder[ErrorResponse] =  Decoder.instance { c =>
    c.downField("error").as[ErrorResponse](deriveDecoder[ErrorResponse])
  }
  implicit val balDecoder: Decoder[BalanceResponse] = deriveDecoder[BalanceResponse]
  implicit val purchaseReqEncoder: ObjectEncoder[PurchaseRequest] = deriveEncoder[PurchaseRequest]
  implicit val transactionInfoDecoder: Decoder[TransactionInfo] = Decoder.instance { c =>
    (c.downField("r").get[Amount]("amount_left_in_wallet"), c.downField("r").get[Identifier]("txid")) mapN TransactionInfo.apply
  }
}
