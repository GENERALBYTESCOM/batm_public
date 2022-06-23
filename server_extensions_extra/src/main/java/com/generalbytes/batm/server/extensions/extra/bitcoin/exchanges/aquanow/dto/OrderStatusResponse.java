package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

import java.math.BigDecimal;

public class OrderStatusResponse {
    public Data data;

    public static class Data {
        public String orderId;
        public String tradeStatus;
        public String statusInfo;
        public BigDecimal priceArrival;
    }

}