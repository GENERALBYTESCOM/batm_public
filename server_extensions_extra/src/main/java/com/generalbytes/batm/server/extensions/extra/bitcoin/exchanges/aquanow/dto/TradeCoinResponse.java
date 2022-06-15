package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

import java.math.BigDecimal;

public class TradeCoinResponse {
    public String type;
    public sellCoin payload;

    public static class sellCoin {
        public String orderId;
        public String receiveCurrency;
        public BigDecimal receiveQuantity;
        public String deliverCurrency;
        public BigDecimal deliverQuantity;
        public BigDecimal fee;
    }

}