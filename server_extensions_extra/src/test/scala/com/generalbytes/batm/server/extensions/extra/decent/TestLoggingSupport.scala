package com.generalbytes.batm.server.extensions.extra.decent

import com.generalbytes.batm.common.LoggingSupport
import org.scalatest.{BeforeAndAfter, TestSuite}

trait TestLoggingSupport extends TestSuite with LoggingSupport with BeforeAndAfter {
  before {
    System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG")
  }
}
