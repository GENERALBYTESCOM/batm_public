package com.generalbytes.batm.server.extensions.extra.digibyte.sources.livecoin;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class LiveCoinTicker {

  @JsonProperty("last")
  private BigDecimal last;

  public BigDecimal getLast() {
    return last;
  }
}