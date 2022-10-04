package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.stillmandigital.dto;

import java.math.BigDecimal;

public class RowBalanceByAssetResponse {

    public String asset;

    public BigDecimal total;

    public BigDecimal free;

    public BigDecimal dailyLimit;

    public BigDecimal netOpenPosition;

}
