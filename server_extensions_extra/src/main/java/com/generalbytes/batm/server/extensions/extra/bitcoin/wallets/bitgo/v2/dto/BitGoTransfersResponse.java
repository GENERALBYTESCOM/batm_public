package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BitGoTransfersResponse {

    @JsonProperty("transfers")
    private List<BitGoTransfer> transfers;

    public List<BitGoTransfer> getTransfers() {
        return transfers;
    }

}
