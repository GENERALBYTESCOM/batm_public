package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitpandapro.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents a side of &#x60;Trade&#x60; or &#x60;Order&#x60;. It can have values &#x60;BUY&#x60; and &#x60;SELL&#x60;.
 */
public enum Side {
  
  BUY("BUY"),
  
  SELL("SELL");

  private String value;

  Side(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static Side fromValue(String value) {
    for (Side b : Side.values()) {
      if (b.value.equals(value)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + value + "'");
  }
}


