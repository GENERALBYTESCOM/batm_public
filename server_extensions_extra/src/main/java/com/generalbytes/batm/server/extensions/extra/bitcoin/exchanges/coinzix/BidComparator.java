package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix;


import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix.dto.entities.OrderBookPrice;

import java.util.Comparator;

public class BidComparator implements Comparator<OrderBookPrice> {
    @Override
    public int compare(OrderBookPrice o1, OrderBookPrice o2) {
        double dif = o2.rate - o1.rate;
        if (dif < 0) return -1;
        if (dif > 0) return 1;
        return 0;
    }
}
