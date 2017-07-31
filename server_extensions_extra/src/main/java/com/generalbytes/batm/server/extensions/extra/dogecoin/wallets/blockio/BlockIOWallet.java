package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IWallet;

import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;
import java.util.*;

import static com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.IBlockIO.PRIORITY_HIGH;
import static com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.IBlockIO.PRIORITY_LOW;
import static com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.IBlockIO.PRIORITY_MEDIUM;

/**
 * Created by b00lean on 8/11/14.
 */
public class BlockIOWallet implements IWallet {
    private String apiKey;
    private String pin;
    private String priority;

    private IBlockIO api;

    public BlockIOWallet(String apiKey, String pin, String priority) {
        this.apiKey = apiKey;
        this.pin = pin;
        if (priority == null) {
            this.priority = PRIORITY_LOW;
        } else if (PRIORITY_LOW.equalsIgnoreCase(priority.trim())) {
            this.priority = PRIORITY_LOW;
        }
        else if (PRIORITY_MEDIUM.equalsIgnoreCase(priority.trim())) {
            this.priority = PRIORITY_MEDIUM;
        }
        else if (PRIORITY_HIGH.equalsIgnoreCase(priority.trim())) {
            this.priority = PRIORITY_HIGH;
        } else {
            this.priority = PRIORITY_LOW;
        }
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
        return ICurrencies.BTC;
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
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
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
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
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        try {
            BlockIOResponseWithdrawal response = api.withdraw(apiKey, pin, amount.toPlainString(), destinationAddress, priority);
            if (response != null && response.getStatus() != null && "success".equalsIgnoreCase(response.getStatus()) && response.getData() != null && response.getData().getTxid() !=null) {
                return response.getData().getTxid();
            }
        }catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

}
