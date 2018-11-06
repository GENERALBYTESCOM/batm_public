package com.generalbytes.batm.common

import cats.effect._
import org.http4s.client.Client
import org.http4s.client.blaze.Http1Client

trait ClientFactory[F[_]] {
  def client(implicit F: ConcurrentEffect[F]): F[Client[F]] = Http1Client[F]()
}
