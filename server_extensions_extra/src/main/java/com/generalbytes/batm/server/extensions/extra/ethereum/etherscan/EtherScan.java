package com.generalbytes.batm.server.extensions.extra.ethereum.etherscan;

import com.generalbytes.batm.server.extensions.extra.ethereum.etherscan.dto.GetTokenTransactionsResponse;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import si.mazi.rescu.RestProxyFactory;

import java.math.BigDecimal;

public class EtherScan {

    protected IEtherscanAPI etherScanApi = RestProxyFactory.createProxy(IEtherscanAPI.class, "https://api.etherscan.io");

    public ReceivedAmount getAddressBalance(String address, String cryptoCurrency) {

        GetTokenTransactionsResponse tokenTransactions = etherScanApi.getTokenTransactions("account", "tokentx", address);

        BigDecimal receivedAmount = tokenTransactions.result.stream()
            .filter(tx -> tx.tokenSymbol.equals(cryptoCurrency))
            .map(tx -> new BigDecimal(tx.value).movePointLeft(Integer.parseInt(tx.tokenDecimal)))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        int confirmations = tokenTransactions.result.stream()
            .mapToInt(tx -> Integer.parseInt(tx.confirmations))
            .min()
            .orElse(0);

        return new ReceivedAmount(receivedAmount, confirmations);
    }

}
