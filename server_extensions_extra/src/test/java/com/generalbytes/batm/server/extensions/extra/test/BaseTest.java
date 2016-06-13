package com.generalbytes.batm.server.extensions.extra.test;

import com.generalbytes.batm.server.extensions.IRateSource;
import com.generalbytes.batm.server.extensions.IRateSourceAdvanced;
import com.generalbytes.batm.server.extensions.extra.test.listener.TestListener;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Listeners;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;


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

    public File getFile(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        File result = new File(classLoader.getResource(fileName).getFile());
        return result;
    }

    protected BigDecimal testRateSourceAndGetLastRate(IRateSource rateSource, String cryptoCurrency, String fiatCurrency) {

        assertNotNull(rateSource, "rateSource is null");
        assertNotNull(cryptoCurrency, "cryptoCurrency is null");
        assertNotNull(fiatCurrency, "fiatCurrency is null");

        assertTrue(rateSource.getCryptoCurrencies().contains(cryptoCurrency), "rateSource doesn't support " + cryptoCurrency);
        assertTrue(rateSource.getFiatCurrencies().contains(fiatCurrency), "rateSource doesn't support " + fiatCurrency);

        final BigDecimal exchangeRateLast = rateSource.getExchangeRateLast(cryptoCurrency, fiatCurrency);
        assertNotNull(exchangeRateLast, "exchangeRateLast is null");
        //Reporter.log("exchangeRateLast (" + fiatCurrency + "/" + cryptoCurrency + "): " + exchangeRateLast.toString(), true);

        if (rateSource instanceof IRateSourceAdvanced) {
            IRateSourceAdvanced rsa = (IRateSourceAdvanced) rateSource;
            final BigDecimal buyPrice = rsa.getExchangeRateForSell(cryptoCurrency, fiatCurrency);
            assertNotNull(buyPrice, "buyPrice is null");
            //Reporter.log("buyPrice: " + buyPrice.toString(), true);

            final BigDecimal sellPrice = rsa.getExchangeRateForSell(cryptoCurrency, fiatCurrency);
            assertNotNull(sellPrice, "sellPrice is null");
            //Reporter.log("sellPrice: " + sellPrice.toString(), true);
        }

        return exchangeRateLast;
    }
}
