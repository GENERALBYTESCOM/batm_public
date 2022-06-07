package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

import java.math.BigDecimal;

public class OrderResponse {
    // not an enum because all possible values not documented
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_PENDING_NEW = "PENDING_NEW";
    public static final String STATUS_FILLED = "FILLED";

    public String id;
    public String status;
    public String statusInfo;
    public BigDecimal fillPrice;

    @Override
    public String toString() {
        return "OrderResponse{" +
            "id='" + id + '\'' +
            ", status='" + status + '\'' +
            ", statusInfo='" + statusInfo + '\'' +
            ", fillPrice=" + fillPrice +
            '}';
    }

    /*
    Response examples:
    - PENDING_NEW:                    {"id":"7782220156104123330","side":"BUY","date":"2021-10-04 14:54:34","pricePerUnit":"220.08000000","quantity":"0.01000000",   "totalValue":"2.20080000",     "market":{"tradeCoin":"LTC","baseCoin":"CAD","symbol":"LTC-CAD","state":null},"status":"PENDING_NEW","leavesQuantity":"0.00000000","fillCount":"0","tradedQuantity":"0.00000000","cancelledQuantity":"0.00000000","statusInfo":null,                 "totalValueTraded":null,"weightedAverageFillPrice":null,"cumulativeFee":null,"type":"MARKET"}
    - FILLED:                         {"id":"7782220156104417848","side":"SELL","date":"2021-10-05 14:20:36","pricePerUnit":"1.35700000","quantity":"0.00009980",    "totalValue":"0.00013542",     "market":{"tradeCoin":"XRP","baseCoin":"CAD","symbol":"XRP-CAD","state":null},"status":"FILLED",      "leavesQuantity":"0.00000000","fillCount":"1","tradedQuantity":"0.00009980","cancelledQuantity":"0.00000000","statusInfo":null,                "totalValueTraded":"0.00013543","weightedAverageFillPrice":"1.35700000","cumulativeFee":"0.00000027","type":"LIMIT"}
    - CANCELLED (insufficient funds): {"id":"0",                  "side":"BUY","date":"2021-10-04 14:59:21","pricePerUnit":"218.41050000","quantity":"1000.00000000","totalValue":"218410.50000000","market":{"tradeCoin":"LTC","baseCoin":"CAD","symbol":"LTC-CAD","state":null},"status":"CANCELLED",   "leavesQuantity":"0.00000000","fillCount":"0","tradedQuantity":"0.00000000","cancelledQuantity":"0.00000000","statusInfo":"Insufficient Funds","totalValueTraded":null,"weightedAverageFillPrice":null,"cumulativeFee":null,"type":"MARKET"}
    */
}