package com.generalbytes.batm.common

import cats.effect.Sync
import cats.syntax.flatMap._
import cats.{Monad, Show}
import com.generalbytes.batm.common.Alias.{ApplicativeErr, Attempt, Error, MonadErr}
import com.generalbytes.batm.common.Util.logObj
import com.generalbytes.batm.common.exchanges.RetryingExchangeWrapper
import org.slf4j.Logger
import retry.Sleep
import shapeless.Typeable
import shapeless.syntax.typeable._

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.util.Try

trait Ops {

  implicit class FuncOps[A, B, C](f: A => B => C) {
    def flip: B => A => C = (b: B) => (a: A) => f(a)(b)
  }

  implicit class Pipe[A](a: => A) {
    def |>[B](f: A => B): B = f(a)
  }

  implicit class AttemptOps[A](a: Attempt[A]) {
    def getOrThrow: A = {
      a.fold(e => throw e, identity)
    }

    def log(implicit logger: Logger, s: Show[A]): Attempt[A] = {
      logObj(a)
    }

    def getOrNull[A1 >: A](implicit ev: Null <:< A1): A1 = a.toOption.orNull

    def cast[T : Typeable]: Attempt[T] = a.flatMap(_.cast[T].toRight(err"Could not cast"))
  }

  implicit class StringContextOps(sc: StringContext) {
    def err(args: Any*): Throwable = Error(sc.s(args: _*))
  }

  implicit class OptionOps[A](a: Option[A]) {
    def getOrThrow(message: String): A = {
      a.fold(throw new Exception(message))(identity)
    }
  }

  implicit class FlattenAttemptOps[F[_] : Monad : ApplicativeErr, A](a: F[Attempt[A]]) {
    def unattempt: F[A] = a.flatMap(_.fold(
      implicitly[ApplicativeErr[F]].raiseError,
      implicitly[ApplicativeErr[F]].pure))
  }

  implicit class ExchangeOps[F[_]: Monad : MonadErr : Sleep : Sync](ex: Exchange[F]) {
    def withRetries(maxRetries: Int): Exchange[F] = new RetryingExchangeWrapper(ex, maxRetries)
  }

  implicit class EitherOps[A](self: Either[A, A]) {
    def value: A = self.fold(identity, identity)
  }

  implicit class SetExtensions[A](self: Set[A]) {
    def toJavaSet: java.util.Set[A] = mutable.Set.apply(self.toList: _*).asJava
  }
}
