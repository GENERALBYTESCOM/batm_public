package com.generalbytes.batm.server.extensions.util;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class OrderBookPriceCalculatorTest {

    private static class Order {
        public final BigDecimal price;
        public final BigDecimal amount;

        private Order(int price, int amount) {
            this.price = new BigDecimal(price);
            this.amount = new BigDecimal(amount);
        }
    }
    private static final OrderBookPriceCalculator<Order> calc = new OrderBookPriceCalculator<>(order -> order.price, order -> order.amount);

    @Test
    public void test() throws IOException {

        List<Order> asks = new ArrayList<>();
        List<Order> bids = new ArrayList<>();

        Assert.assertThrows(IllegalArgumentException.class, () -> calc.getBuyPrice(BigDecimal.ONE, asks));
        Assert.assertThrows(IllegalArgumentException.class, () -> calc.getSellPrice(BigDecimal.ONE, bids));

        bids.add(new Order(50, 2));
        bids.add(new Order(70, 2));
        bids.add(new Order(90, 2));

        asks.add(new Order(110, 2));
        asks.add(new Order(130, 2));
        asks.add(new Order(150, 2));

        Assertions.assertThat(calc.getBuyPrice(new BigDecimal("0.001"), asks)).isEqualByComparingTo("110");
        Assertions.assertThat(calc.getSellPrice(new BigDecimal("0.001"), bids)).isEqualByComparingTo("90");

        Assertions.assertThat(calc.getBuyPrice(new BigDecimal("3"), asks)).isEqualByComparingTo("130");
        Assertions.assertThat(calc.getSellPrice(new BigDecimal("3"), bids)).isEqualByComparingTo("70");

        Assertions.assertThat(calc.getBuyPrice(new BigDecimal("5"), asks)).isEqualByComparingTo("150");
        Assertions.assertThat(calc.getSellPrice(new BigDecimal("5"), bids)).isEqualByComparingTo("50");

        Assertions.assertThat(calc.getBuyPrice(new BigDecimal("6"), asks)).isEqualByComparingTo("150");
        Assertions.assertThat(calc.getSellPrice(new BigDecimal("6"), bids)).isEqualByComparingTo("50");

        Assert.assertThrows(IllegalArgumentException.class, () -> calc.getBuyPrice(new BigDecimal("6.1"), asks));
        Assert.assertThrows(IllegalArgumentException.class, () -> calc.getSellPrice(new BigDecimal("6.1"), bids));

    }

}