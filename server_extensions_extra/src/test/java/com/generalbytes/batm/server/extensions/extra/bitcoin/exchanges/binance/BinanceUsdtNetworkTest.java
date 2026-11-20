package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.binance;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class BinanceUsdtNetworkTest {

    private static Stream<Arguments> ofArguments() {
        return Stream.of(
            arguments("ETH", BinanceUsdtNetwork.ETH),
            arguments("eth", BinanceUsdtNetwork.ETH),
            arguments("eth  ", BinanceUsdtNetwork.ETH),
            arguments(" EtH ", BinanceUsdtNetwork.ETH),
            arguments("BSC", BinanceUsdtNetwork.BSC),
            arguments("bsc", BinanceUsdtNetwork.BSC),
            arguments(" Bsc ", BinanceUsdtNetwork.BSC),
            arguments(null, BinanceUsdtNetwork.ETH),
            arguments("", BinanceUsdtNetwork.ETH),
            arguments("  ", BinanceUsdtNetwork.ETH)
        );
    }

    @ParameterizedTest
    @MethodSource("ofArguments")
    void testOf(String value, BinanceUsdtNetwork expectedNetwork) {
        assertThat(BinanceUsdtNetwork.of(value)).isEqualTo(expectedNetwork);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ERC20", "BEP20", "BNB", "TRX", "BSC2"})
    void testOf_UnsupportedNetwork(String value) {
        assertThatThrownBy(() -> BinanceUsdtNetwork.of(value))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unsupported USDT network: " + value);
    }
}
