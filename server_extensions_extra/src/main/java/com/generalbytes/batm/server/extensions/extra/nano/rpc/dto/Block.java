package com.generalbytes.batm.server.extensions.extra.nano.rpc.dto;

import java.math.BigInteger;

public record Block(String type, String account, BigInteger amount, String hash) {
    public static final String TYPE_RECEIVE = "receive";
}
