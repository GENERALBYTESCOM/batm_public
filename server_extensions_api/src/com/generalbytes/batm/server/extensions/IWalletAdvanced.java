package com.generalbytes.batm.server.extensions;

import java.math.BigDecimal;

/**
 * Created by b00lean on 22.4.15.
 */
public interface IWalletAdvanced extends IWallet{
    public String sendCoins(String destinationAddress, BigDecimal amount, BigDecimal fee ,String cryptoCurrency, String description); //returns txid
}
