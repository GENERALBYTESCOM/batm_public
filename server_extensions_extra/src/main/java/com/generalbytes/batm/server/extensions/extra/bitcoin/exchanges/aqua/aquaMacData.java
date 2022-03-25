package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aqua;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import si.mazi.rescu.RestInvocation;

@JsonPropertyOrder({"method", "path", "content-length", "query"})
class aquaMacData {
    @JsonProperty("path")
    public String path;
    @JsonProperty("content-length")
    public int contentLength;
    @JsonProperty("query")
    public String query;

    public static aquaMacData from(RestInvocation restInvocation) {
        aquaMacData macData = new aquaMacData();
        macData.path = restInvocation.getPath();
        macData.contentLength = getBodyLength(restInvocation.getRequestBody());
        macData.query = restInvocation.getQueryString();
        return macData;
    }

    private static int getBodyLength(String requestBody) {
        return (requestBody == null || requestBody.isEmpty()) ? -1 : requestBody.length();
    }
}
