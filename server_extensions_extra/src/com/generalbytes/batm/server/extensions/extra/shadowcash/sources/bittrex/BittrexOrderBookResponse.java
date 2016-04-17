package com.generalbytes.batm.server.extensions.extra.shadowcash.sources.bittrex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.knowm.xchange.bittrex.v1.dto.marketdata.BittrexDepth;

/**
 * @author ludx
 */
@Data
public class BittrexOrderBookResponse {
    @JsonProperty("success")
    private boolean success;
    @JsonProperty("message")
    private String message;
    @JsonProperty("result")
    private BittrexDepth depth;
}
