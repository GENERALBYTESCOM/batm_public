package com.generalbytes.batm.server.extensions.extra.nano.rpc.dto;

import java.math.BigInteger;

public record AccountBalance(BigInteger confBalance, BigInteger unconfBalance, BigInteger unconfPending) {
}
