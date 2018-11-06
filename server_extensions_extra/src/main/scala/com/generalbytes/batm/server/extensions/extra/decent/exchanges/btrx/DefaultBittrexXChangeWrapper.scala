package com.generalbytes.batm.server.extensions.extra.decent.exchanges.btrx

import cats.effect._
import cats.effect.implicits._
import cats.implicits._
import cats.{Monad, Show}
import com.generalbytes.batm.common.Alias._
import com.generalbytes.batm.common.Util._
import com.generalbytes.batm.common._
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.server.extensions.extra.decent.extension.LoginInfo
import com.generalbytes.batm.server.extensions.extra.decent.sources.btrx.{BittrexTick, FallbackBittrexTicker}
import org.http4s.Uri
import org.knowm.xchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.bittrex.BittrexExchange
import org.knowm.xchange.bittrex.dto.account.BittrexOrder
import org.knowm.xchange.bittrex.service.BittrexAccountServiceRaw
import org.knowm.xchange.dto.Order.OrderType
import org.knowm.xchange.dto.trade.LimitOrder
import retry._

class DefaultBittrexXChangeWrapper[F[_]: Sync : ApplicativeErr : Monad : Sleep : ConcurrentEffect](credentials: LoginInfo)
  extends Exchange[F] with LoggingSupport {

  import DefaultBittrexXChangeWrapper._
  import XChangeConversions._

  protected val exchange: xchange.Exchange = createExchange

  protected def createExchange: xchange.Exchange = {
    val spec = new BittrexExchange().getDefaultExchangeSpecification
    spec.setApiKey(credentials.apiKey)
    spec.setSecretKey(credentials.secretKey)
    val result = ExchangeFactory.INSTANCE.createExchange(spec)
    assert(result != null)
    result
  }

  private def multiplicativeFactor(orderType: OrderType): ExchangeRate = {
    val additive = 0.2
    orderType match {
      case OrderType.BID => 1d + additive |> BigDecimal.valueOf
      case OrderType.ASK => 1d - additive |> BigDecimal.valueOf
    }
  }

  def getOrder(id: Identifier): F[BittrexOrder] = Sync[F].delay {
    exchange.getAccountService.asInstanceOf[BittrexAccountServiceRaw].getBittrexOrder(id)
  }

  override def getBalance(currency: Currency): F[Amount] = Sync[F].delay {
    val balance = exchange.getAccountService.getAccountInfo.getWallet.getBalance(currency.convert)
    BigDecimal(balance.getTotal)
  } flatTap (b => log(b, s"Balance in $currency"))

  override def getAddress(currency: Currency): F[Address] = raise[F](err"Not implemented")

  // TODO: Make cancellable
  // TODO: IO.shift to another thread to avoid blocking
  override def fulfillOrder(order: TradeOrder): F[Identifier] = {
    val orderId: F[Identifier] = Async.memoize {
      createLimitOrder(order).map(exchange.getTradeService.placeLimitOrder)
    }.toIO.unsafeRunSync() // only to initialize memoization, doesn't make the actual call

    val maxAttempts = 10
    val polling = retryingM[BittrexOrder](
      RetryPolicies.limitRetries[F](maxAttempts),
      r => !r.getIsOpen,
      logOp[F, BittrexOrder]) {
      for {
        ordId <- orderId
        _ <- log(ordId, s"Order ID for $order ")
        order <- getOrder(ordId)
      } yield order
    } map (_.getOrderUuid)

    polling.handleErrorWith(e => raise[F](ErrorDecorator(e, order.currencyPair)))
  }

  override def withdrawFunds(currency: Currency, amount: Amount, destination: Address): F[Identifier] = {
    delay {
      exchange.getAccountService.withdrawFunds(currency.convert, amount.bigDecimal, destination)
    }
  }

  protected def createLimitOrder(order: TradeOrder): F[LimitOrder] = {
    val ticker = new FallbackBittrexTicker[F](order.currencyPair)
    for {
      rate <- ticker.currentRates
      orderType = getOrderType(order)
      amount = order.amount.amount
      price = calculateLimitPrice(rate, orderType)
      _ <- log(price, "limit price")
    } yield new LimitOrder.Builder(orderType, order.currencyPair.convert)
      .tradableAmount(amount.bigDecimal)
      .limitPrice(price.bigDecimal)
      .build()
  }

  // TODO: Exhaustive match
  // NOTE: We specify the limit price to the OPPOSITE ticker ask->bid bid->ask, because there's no market order
  protected def calculateLimitPrice(tick: BittrexTick, orderType: OrderType): Amount = {
    val rateSelector = getRateSelector(orderType.inverse)
    val mulFactor = multiplicativeFactor(orderType)
    val rate = rateSelector(tick) * mulFactor
    val significantDigits = 4
    val newScale = significantDigits - rate.precision + rate.scale
    rate.setScale(newScale, scala.BigDecimal.RoundingMode.HALF_UP)
  }

  override val cryptoCurrencies: Set[CryptoCurrency] = Set.empty
  override val fiatCurrencies: Set[FiatCurrency] = Set.empty
  override val preferredFiat: FiatCurrency = Currency.USDollar
}

object DefaultBittrexXChangeWrapper {
  case class ErrorDecorator(error: Throwable, currencyPair: CurrencyPair) extends Throwable(error)

  implicit val showBittrexOrder: Show[BittrexOrder] = Show.fromToString
  implicit val orderTypeShow: Show[OrderType] = Show.fromToString
}

