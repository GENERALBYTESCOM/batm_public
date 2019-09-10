package com.generalbytes.batm.server.extensions.extra.futurocoin.sources.yobit.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.util.HashMap;

public class YobitResponse extends HashMap<String, Ticker> {

    @JsonAnySetter
    public void set(String name, Ticker value) {
        put(name, value);
    }

}
