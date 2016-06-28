package com.generalbytes.batm.server.extensions.extra.test;

import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.extra.test.listener.TestListener;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Listeners;

import java.io.IOException;
import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author ludx
 */
@Listeners({TestListener.class})
public abstract class BaseTest {

    public String getFileAsString(String fileName) {
        String result = "";
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected BigDecimal testRateSourceAndGetLastRate(IRateSource rateSource, String cryptoCurrency, String fiatCurrency) {
        assertThat(rateSource, is(notNullValue()));
        assertThat(cryptoCurrency, is(allOf(notNullValue(), instanceOf(String.class))));
        assertThat(fiatCurrency, is(allOf(notNullValue(), instanceOf(String.class))));
        assertThat(rateSource.getCryptoCurrencies(), contains(cryptoCurrency));
        assertThat(rateSource.getFiatCurrencies(), contains(fiatCurrency));

        final BigDecimal exchangeRateLast = rateSource.getExchangeRateLast(cryptoCurrency, fiatCurrency);
        assertThat(exchangeRateLast, is(allOf(notNullValue(), instanceOf(BigDecimal.class))));

        if (rateSource instanceof IRateSourceAdvanced) {
            IRateSourceAdvanced rsa = (IRateSourceAdvanced) rateSource;
            final BigDecimal buyPrice = rsa.getExchangeRateForSell(cryptoCurrency, fiatCurrency);
            final BigDecimal sellPrice = rsa.getExchangeRateForSell(cryptoCurrency, fiatCurrency);

            assertThat(buyPrice, is(allOf(notNullValue(), instanceOf(BigDecimal.class))));
            assertThat(sellPrice, is(allOf(notNullValue(), instanceOf(BigDecimal.class))));
        }

        return exchangeRateLast;
    }
}
