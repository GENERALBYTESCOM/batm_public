package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.dto.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Currency {
    public String iso3;
    public String name;
    public long refill;
    public long withdraw;

    @JsonIgnore
    public String networks;
}
