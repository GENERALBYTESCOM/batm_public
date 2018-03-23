package com.generalbytes.batm.server.extensions.extra.flashcoin.sources.coinexchange;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Created by vsobalski on 15/02/18.
 */

/**
 {
 "success": "1",
 "request": "/api/v1/getmarket",
 "message": "",
 "result": {
     "MarketID": "684",
     "LastPrice": "0.00000373",
     "Change": "-4.85",
     "HighPrice": "0.00000461",
     "LowPrice": "0.00000372",
     "Volume": "0.13383691",
     "BTCVolume": "0.13383691",
     "TradeCount": "20",
     "BidPrice": "0.00000374",
     "AskPrice": "0.00000447",
     "BuyOrderCount": "50",
     "SellOrderCount": "255"
    }
 }
**/
public class OrderBookResponse {
    public int success;
    public String message;

    @JsonProperty("result")
    public  CResult result;


    public class CResult {
        public BigDecimal MarketID;
        public BigDecimal LastPrice;
        public BigDecimal Change;
        public BigDecimal HighPrice;
        public BigDecimal LowPrice;
        public BigDecimal Volume;
        public BigDecimal BTCVolume;
        public BigDecimal TradeCount;
        public BigDecimal BidPrice;
        public BigDecimal AskPrice;
        public BigDecimal BuyOrderCount;
        public BigDecimal SellOrderCount;
    }

}
