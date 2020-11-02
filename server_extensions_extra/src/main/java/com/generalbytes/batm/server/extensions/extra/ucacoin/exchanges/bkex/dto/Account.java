package com.generalbytes.batm.server.extensions.extra.ucacoin.exchanges.bkex.dto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Account details of a registered user, including balances.
 **/
public class Account {
    private List<Balance> balances;

    @JsonProperty("msg")
    private String msg;

    @JsonProperty("code")
    private String code;

    @JsonProperty("data")
    private void unpackBalances(Map<String, String> data) throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        balances = mapper.readValue(data.get("WALLET"), new TypeReference<List<Balance>>(){});
    }


    public List<Balance> getBalances() {
        return balances;
    }

    public void setBalances(
        List<Balance> balances) {
        this.balances = balances;
    }

    @Override public String toString() {
        return "Account{" +
            " balances=" + balances +
            ", code=" + code +"}";
    }
}

