package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.digifinex.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a side of &#x60;Trade&#x60; or &#x60;Order&#x60;. It can have values &#x60;BUY&#x60; and &#x60;SELL&#x60;.
 */
public class Symbol {
  
    @JsonProperty("symbol")
    private String symbol;

    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }
 
    public String getSymbol() {
      return symbol;
    }

    @Override public String toString() {
      return symbol;
  }
}


