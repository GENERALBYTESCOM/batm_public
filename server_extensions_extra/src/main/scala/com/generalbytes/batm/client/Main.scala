package com.generalbytes.batm.client

import cats.Show
import cats.implicits._
import com.generalbytes.batm.common.Alias.Task
import com.generalbytes.batm.common.implicits._
import com.generalbytes.batm.common.{Currency, Util}
import com.generalbytes.batm.server.extensions.extra.decent.exchanges.btrx.{DefaultBittrexXChangeWrapper, OrderChainingBittrexXChangeWrapper}
import com.generalbytes.batm.server.extensions.extra.decent.extension.LoginInfo
import org.knowm.xchange.dto.trade.UserTrade
import org.slf4j.{Logger, LoggerFactory}

import scala.io.StdIn
import scala.language.postfixOps
import scala.util.Try

object Main extends App {

}
