package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.dto.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetDepositAddressRequest extends BasicRequest{

    public String iso;

    @JsonProperty("new")
    public int isNew = 0;
}
