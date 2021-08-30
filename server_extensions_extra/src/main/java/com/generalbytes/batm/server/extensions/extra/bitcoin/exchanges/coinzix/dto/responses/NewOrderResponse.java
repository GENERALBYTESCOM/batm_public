package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.dto.responses;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.dto.entities.NewOrderId;

public class NewOrderResponse extends BasicResponse {
    public String message;
    public NewOrderId data;
}
