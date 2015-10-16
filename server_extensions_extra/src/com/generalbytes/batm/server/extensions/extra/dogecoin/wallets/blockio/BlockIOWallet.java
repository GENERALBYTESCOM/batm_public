package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IWallet;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.SimpleLoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.slf4j.spi.LocationAwareLogger;
import si.mazi.rescu.RestProxyFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by b00lean on 8/11/14.
 */
public class BlockIOWallet implements IWallet {
    private String apiKey;
    private String pin;

    private IBlockIO api;

    public BlockIOWallet(String apiKey, String pin) {
        this.apiKey = apiKey;
        this.pin = pin;
        api = RestProxyFactory.createProxy(IBlockIO.class, "https://block.io");
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<String>();
        result.add(ICurrencies.BTC);
        result.add(ICurrencies.LTC);
        result.add(ICurrencies.DOGE);
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return ICurrencies.DOGE;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!(cryptoCurrency.equalsIgnoreCase(ICurrencies.DOGE) || cryptoCurrency.equalsIgnoreCase(ICurrencies.BTC) || cryptoCurrency.equalsIgnoreCase(ICurrencies.LTC))) {
            return null;
        }
        try {
            BlockIOResponseAddresses response = api.getAddresses(apiKey);
            if (response != null && response.getData() != null && response.getData().getAddresses() != null && response.getData().getAddresses().length> 0) {
                return response.getData().getAddresses()[0].getAddress();
            }
        }catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }


    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!(cryptoCurrency.equalsIgnoreCase(ICurrencies.DOGE) || cryptoCurrency.equalsIgnoreCase(ICurrencies.BTC) || cryptoCurrency.equalsIgnoreCase(ICurrencies.LTC))) {
            return null;
        }
        try {
            BlockIOResponseBalance response = api.getBalance(apiKey);
            if (response != null && response.getData() != null && response.getData().getAvailable_balance() != null ) {
                return new BigDecimal(response.getData().getAvailable_balance());
            }
        }catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        if (!(cryptoCurrency.equalsIgnoreCase(ICurrencies.DOGE) || cryptoCurrency.equalsIgnoreCase(ICurrencies.BTC) || cryptoCurrency.equalsIgnoreCase(ICurrencies.LTC))) {
            return null;
        }
        try {
            BlockIOResponseWithdrawal response = api.withdraw(apiKey, pin, amount.toPlainString(), destinationAddress);
            if (response != null && response.getStatus() != null && "success".equalsIgnoreCase(response.getStatus()) && response.getData() != null && response.getData().getTxid() !=null) {
                return response.getData().getTxid();
            }
        }catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

}
