package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.aquanow;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import si.mazi.rescu.RestInvocation;
import com.google.gson.Gson;


import javax.ws.rs.HeaderParam;

// THE ORDER OF THE FIELDS IS IMPORTANT!
// The bitbuy documentation uses JSONObject which uses HashMap where elements are unordered
// but the MAC is computed over the resulting JSON string
@JsonPropertyOrder({"httpMethod", "path","nonce"})
class AquaNowMacData {

    @JsonProperty("httpMethod")
    public String httpMethod;
    @JsonProperty("path")
    public String path;
    @JsonProperty("nonce")
    public String nonce;

    public static String from(RestInvocation restInvocation) {
        AquaNowMacData macData = new AquaNowMacData();
        macData.httpMethod = restInvocation.getHttpMethod().toUpperCase();
        macData.path = restInvocation.getPath();
        macData.nonce = (String) restInvocation.getParamValue(HeaderParam.class, "x-nonce");
        Gson gson = new Gson();
        String macDataString = gson.toJson(macData);
        return macDataString;
    }

}
