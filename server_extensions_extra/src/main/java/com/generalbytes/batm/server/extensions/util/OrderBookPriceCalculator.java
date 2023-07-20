package com.generalbytes.batm.server.extensions.util;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Calculates buy or sell price from order book orders (bids or asks) downloaded from an exchange.
 * The price depends on the amount you want to buy or sell,
 * e.g. the price of one BTC when buying 10 BTC might be higher than when buying just 1 BTC.
 *
 * @param <T> Type of the order book order objects used to calculate the price.
 *            It must be possible to obtain price and amount of each order.
 */
public class OrderBookPriceCalculator<T> {
    private final Comparator<T> asksComparator;
    private final Comparator<T> bidsComparator;
    private final Function<T, BigDecimal> orderLimitPriceGetter;
    private final Function<T, BigDecimal> orderAmountGetter;

    /**
     * @param orderLimitPriceGetter a function that retrieves limit price (price per unit) of the order in the order book
     * @param orderAmountGetter     a function that retrieves cryptocurrency amount (quantity) of the order in the order book
     */
    public OrderBookPriceCalculator(Function<T, BigDecimal> orderLimitPriceGetter, Function<T, BigDecimal> orderAmountGetter) {
        // bids: highest price first (used for sell)
        // asks: lowest price first (used for buy)
        this.asksComparator = Comparator.comparing(orderLimitPriceGetter);
        this.bidsComparator = Comparator.comparing(orderLimitPriceGetter).reversed();
        this.orderLimitPriceGetter = orderLimitPriceGetter;
        this.orderAmountGetter = orderAmountGetter;
    }

    /**
     * Calculates the price (per one unit of cryptocurrency) we would get if we wanted to SELL the given amount.
     *
     * @param bids bids from the order book.
     *             Bids are other people's BUY orders (we are selling, others are buying).
     *             Bids prices are lower than the last traded price (the midpoint price).
     *             We use the bids with the highest prices first until we reach the required amount.
     */
    public BigDecimal getSellPrice(BigDecimal cryptoAmount, List<T> bids) {
        return getPrice(cryptoAmount, bids, bidsComparator);
    }

    /**
     * Calculates the price (per one unit of cryptocurrency) we would have to pay if we wanted to BUY the given amount.
     *
     * @param asks asks from the order book.
     *             Asks are other people's SELL orders (we are buying, others are selling).
     *             Asks prices are higher than the last traded price (the midpoint price).
     *             We use the asks with the lowest prices first until we reach the required amount.
     */
    public BigDecimal getBuyPrice(BigDecimal cryptoAmount, List<T> asks) {
        return getPrice(cryptoAmount, asks, asksComparator);
    }

    private BigDecimal getPrice(BigDecimal cryptoAmount, List<T> orders, Comparator<T> comparator) {
        Objects.requireNonNull(orders, "orders list cannot be null");
        List<T> sorted = orders.stream().sorted(comparator).collect(Collectors.toList());
        BigDecimal total = BigDecimal.ZERO;
        for (T order : sorted) {
            total = total.add(orderAmountGetter.apply(order));
            if (cryptoAmount.compareTo(total) <= 0) {
                return orderLimitPriceGetter.apply(order);
            }
        }
        throw new IllegalArgumentException("tradable price not available");
    }
}
