package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import si.mazi.rescu.RestInvocation;

// THE ORDER OF THE FIELDS IS IMPORTANT!
// The bitbuy documentation uses JSONObject which uses HashMap where elements are unordered
// but the MAC is computed over the resulting JSON string
@JsonPropertyOrder({"path", "content-length", "query"})
class BitbuyMacData {
    @JsonProperty("path")
    public String path;
    @JsonProperty("content-length")
    public int contentLength;
    @JsonProperty("query")
    public String query;

    public static BitbuyMacData from(RestInvocation restInvocation) {
        BitbuyMacData macData = new BitbuyMacData();
        macData.path = restInvocation.getPath();
        macData.contentLength = getBodyLength(restInvocation.getRequestBody());
        macData.query = restInvocation.getQueryString();
        return macData;
    }

    private static int getBodyLength(String requestBody) {
        return (requestBody == null || requestBody.isEmpty()) ? -1 : requestBody.length();
    }
}
