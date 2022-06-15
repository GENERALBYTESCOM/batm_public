package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow.dto;

import java.math.BigDecimal;

public class GetAddressRequest {
    public boolean isAutopilotAddress;
    public String symbol;

    public GetAddressRequest(String symbol) {
        this.isAutopilotAddress = false;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return "GetAddressRequest{" +
            "isAutopilotAddress=" + isAutopilotAddress +
            ", symbol='" + symbol + '\'' +
            '}';
    }

}
