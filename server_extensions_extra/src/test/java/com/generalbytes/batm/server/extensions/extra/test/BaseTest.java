package com.generalbytes.batm.server.extensions.extra.test;

import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.extra.test.listener.TestListener;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.apache.commons.io.IOUtils;
import org.testng.Reporter;
import org.testng.annotations.Listeners;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author ludx
 */
@Listeners({TestListener.class})
public abstract class BaseTest {

    protected String getFileAsString(String fileName) {
        String result = "";
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected File getFile(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        File result = new File(classLoader.getResource(fileName).getFile());
        return result;
    }

    protected BigDecimal testRateSourceAndGetLastRate(IRateSource rateSource, String cryptoCurrency, String fiatCurrency) {
        assertThat(rateSource, is(notNullValue()));
        assertThat(cryptoCurrency, is(allOf(notNullValue(), instanceOf(String.class))));
        assertThat(fiatCurrency, is(allOf(notNullValue(), instanceOf(String.class))));
        assertThat(rateSource.getCryptoCurrencies(), hasItem(cryptoCurrency));
        assertThat(rateSource.getFiatCurrencies(), hasItem(fiatCurrency));

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

    protected void printLoggedRequests() {
        List<LoggedRequest> requests = findAll(postRequestedFor(urlMatching("/.*")));
        requests.addAll(findAll(getRequestedFor(urlMatching("/.*"))));
        Reporter.log("\n\n\n\nlogged requests: " + requests.size(), true);
        for (LoggedRequest loggedRequest : requests) {
            Reporter.log("BODY: " + loggedRequest.getBodyAsString(), true);
            Reporter.log("URL: " + loggedRequest.getAbsoluteUrl(), true);
            Reporter.log("HEADERS: " + loggedRequest.getHeaders().toString(), true);
        }
    }

}
