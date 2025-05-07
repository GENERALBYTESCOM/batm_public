package com.generalbytes.batm.server.extensions.extra.nano.rpc.dto;

import java.math.BigInteger;

public record Block(String type, String account, BigInteger amount) {
}
