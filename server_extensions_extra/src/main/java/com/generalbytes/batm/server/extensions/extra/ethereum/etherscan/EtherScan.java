package com.generalbytes.batm.server.extensions.extra.ethereum.etherscan;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.ethereum.etherscan.GetTransactionsResponse.Transaction;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class EtherScan {

    private static final int ETH_DECIMAL = 18;
    private final IEtherscanAPI etherScanApi;

    public EtherScan() {
        this(null);
    }
    public EtherScan(String apiKey) {
        etherScanApi = IEtherscanAPI.create(apiKey);
    }

    public ReceivedAmount getAddressBalance(String address, String cryptoCurrency) throws IOException {
        if (CryptoCurrency.ETH.getCode().equals(cryptoCurrency)) {
            return getEthReceivedAmount(address);
        }
        return getTokenReceivedAmount(address, cryptoCurrency);
    }

    private ReceivedAmount getTokenReceivedAmount(String address, String cryptoCurrency) throws IOException {
        List<Transaction> transactions = etherScanApi.getTokenTransactions(address).result;

        BigDecimal receivedAmount = transactions.stream()
            .filter(tx -> tx.tokenSymbol.equals(cryptoCurrency))
            .map(tx -> new BigDecimal(tx.value).movePointLeft(Integer.parseInt(tx.tokenDecimal))) // TODO check contactAddress instead
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .stripTrailingZeros();

        int confirmations = getConfirmations(transactions);

        return new ReceivedAmount(receivedAmount, confirmations);
    }

    private ReceivedAmount getEthReceivedAmount(String address) throws IOException {
        List<Transaction> transactions = etherScanApi.getTransactions(address).result;

        BigDecimal receivedAmount = transactions.stream()
            .map(tx -> new BigDecimal(tx.value).movePointLeft(ETH_DECIMAL))
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .stripTrailingZeros();

        int confirmations = getConfirmations(transactions);

        return new ReceivedAmount(receivedAmount, confirmations);
    }

    private int getConfirmations(List<Transaction> transactions) {
        return transactions.stream()
            .mapToInt(tx -> Integer.parseInt(tx.confirmations))
            .min()
            .orElse(0);
    }

}
