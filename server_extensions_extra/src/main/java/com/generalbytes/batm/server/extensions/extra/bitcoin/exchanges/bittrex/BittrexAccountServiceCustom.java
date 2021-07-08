package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex;

import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.dto.BittrexNewWithdrawal;
import com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bittrex.dto.BittrexWithdrawal;
import org.knowm.xchange.bittrex.BittrexErrorAdapter;
import org.knowm.xchange.bittrex.BittrexExchange;
import org.knowm.xchange.bittrex.dto.BittrexException;
import org.knowm.xchange.bittrex.service.BittrexAccountService;
import org.knowm.xchange.client.ResilienceRegistries;
import org.knowm.xchange.currency.Currency;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Temporary solution. Remove when withdrawal will be available in org.knowm.xchange:xchange-bitfinex
 */
//TODO: BATM-2327 remove this class
public class BittrexAccountServiceCustom extends BittrexAccountService {

    private final BittrexAuthenticatedCustom bittrexAuthenticatedCustom;

    /**
     * Constructor
     *
     * @param exchange
     * @param bittrex
     * @param resilienceRegistries
     */
    public BittrexAccountServiceCustom(BittrexExchange exchange, BittrexAuthenticatedCustom bittrex, ResilienceRegistries resilienceRegistries) {
        super(exchange, bittrex, resilienceRegistries);
        this.bittrexAuthenticatedCustom = bittrex;
    }

    @Override
    public String withdrawFunds(Currency currency, BigDecimal amount, String address) throws IOException {
        try {
            return createNewWithdrawal(currency, amount, address).getId();
        } catch (BittrexException e) {
            throw BittrexErrorAdapter.adapt(e);
        }
    }

    private BittrexWithdrawal createNewWithdrawal(Currency currency, BigDecimal amount, String address) throws IOException {
        BittrexNewWithdrawal newWithdrawal = new BittrexNewWithdrawal();
        newWithdrawal.setCurrencySymbol(currency.getCurrencyCode());
        newWithdrawal.setQuantity(amount);
        newWithdrawal.setCryptoAddress(address);

        return this.bittrexAuthenticatedCustom.createNewWithdrawal(
            this.apiKey,
            System.currentTimeMillis(),
            this.contentCreator,
            this.signatureCreator,
            newWithdrawal
        );
    }
}
