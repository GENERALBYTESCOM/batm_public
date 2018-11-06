package com.generalbytes.batm.common

import com.generalbytes.batm.common.Alias.Attempt
import com.generalbytes.batm.common.implicits._

sealed trait Currency {
  val name: String
}

sealed trait Fiat { self: Currency => }
sealed trait Crypto { self: Currency => }

trait FiatCurrency extends Currency with Fiat
trait CryptoCurrency extends Currency with Crypto

object Currency {
  val cryptos: Set[CryptoCurrency] = Set(Bitcoin, Decent)
  val fiats: Set[FiatCurrency] = Set(Euro, USDollar)

  def withName(currency: String): Attempt[Currency] = {
    allMap.get(currency).toRight(err"Currency with name $currency not found")
  }

  def apply[T <: Currency : Default]: T = implicitly[Default[T]].value

  trait DCT extends CryptoCurrency { val name = "DCT" }
  trait BTC extends CryptoCurrency { val name = "BTC" }
  case object Decent extends DCT
  case object Bitcoin extends BTC

  trait EUR extends FiatCurrency { val name = "EUR" }
  trait USD extends FiatCurrency { val name = "USD" }
  case object Euro extends EUR
  case object USDollar extends USD

  trait Default[T <: Currency] {
    val value: T
  }

  val all: Set[Currency] = Set(Decent, Bitcoin, Euro, USDollar)
  val allMap: Map[String, Currency] = all.map(c => c.name -> c).toMap

  implicit val dct: Default[DCT] = new Default[DCT] { val value: DCT = Decent }
  implicit val btc: Default[BTC] = new Default[BTC] { val value: BTC = Bitcoin }
  implicit val eur: Default[EUR] = new Default[EUR] { val value: EUR = Euro }
  implicit val usd: Default[USD] = new Default[USD] { val value: USD = USDollar }
}
