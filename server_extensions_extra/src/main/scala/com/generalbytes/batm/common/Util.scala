package com.generalbytes.batm.common

import cats.{ApplicativeError, Monad, Show}
import cats.effect.Sync
import com.generalbytes.batm.common.Alias.{ApplicativeErr, Attempt, Task}
import org.slf4j.Logger
import retry.RetryDetails

import scala.language.higherKinds

object Util {

  def logObj[A: Show](self: Attempt[A])(implicit logger: Logger): Attempt[A] = {
    self.left.foreach(x => logger.error(x.toString))
    self.right.foreach(x => logger.debug(s"Value: ${Show[A].show(x)}"))
    self
  }

  def logIO[A](a: A)(implicit logger: Logger): Task[A] = log[Task, A](a)

  def log[F[_]: Sync, A](a: A, message: String = "Value")(implicit logger: Logger): F[A] = implicitly[Sync[F]].delay {
    logger.debug(s"$message: $a")
    a
  }

  implicit val showThrowable: Show[Throwable] = Show.fromToString

  class RaiseAux[F[_]] {
    def apply[A](e: Throwable)(implicit F: ApplicativeErr[F]): F[A] = ApplicativeErr[F].raiseError(e)
  }

  def raise[F[_]] = new RaiseAux[F]
  def delay[F[_]: Sync, A](thunk: => A): F[A] = Sync[F].delay(thunk)

  def logOp[M[_]: Monad : Sync, A: Show](implicit loggger: Logger): (A, RetryDetails) => M[Unit] =
    (a, _) => implicitly[Monad[M]].map(log(a))(_ => ())

  private val hmacSHA256 = "HmacSHA256"
  private val hmacSHA512 = "HmacSHA512"

  def hmacsha256(message: String, secretKey: String): String = hmacsha(message, secretKey, hmacSHA256)
  def hmacsha512(message: String, secretKey: String): String = hmacsha(message, secretKey, hmacSHA512)

  private def hmacsha(message: String, secretKey: String, algorithm: String): String = {
    import javax.crypto.Mac
    import javax.crypto.spec.SecretKeySpec
    val sha256_HMAC = Mac.getInstance(algorithm)
    val secret_key = new SecretKeySpec(secretKey.getBytes, hmacSHA256)
    sha256_HMAC.init(secret_key)

    Hex.valueOf(sha256_HMAC.doFinal(message.getBytes))
  }
}

object Hex {
  def valueOf(buf: Array[Byte]): String = buf.map("%02X" format _).mkString
}
