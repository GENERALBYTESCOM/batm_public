package com.generalbytes.batm.common

import cats.effect.IO
import cats.{ApplicativeError, Id, MonadError, ~>}

object Alias {
  case class Error(message: String) extends Throwable(message)

  type Identifier = String
  type Address = String
  type Amount = BigDecimal
  type Task[+A] = IO[A]
  val Task = IO
  type Attempt[+A] = Either[Throwable, A]
  type ExchangeRate = BigDecimal
  type ApplicativeErr[F[_]] = ApplicativeError[F, Throwable]
  def ApplicativeErr[F[_] : ApplicativeErr]: ApplicativeErr[F] = ApplicativeError[F, Throwable]
  type MonadErr[F[_]] = MonadError[F, Throwable]
  type Interpreter[F[_]] = F ~> Id
  type Translator[F[_]] = Attempt ~> F

  object Interpreter { def apply[F[_]](implicit ev: F ~> Id): Interpreter[F] = ev }

  object Translator { def apply[F[_]](implicit ev: Attempt ~> F): Translator[F] = ev }
}
