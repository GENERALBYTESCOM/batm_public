package com.generalbytes.batm.server.extensions.extra.ethereum.etherscan.dto;

import java.util.List;

public class GetTokenTransactionsResponse {
    public String status;
    public String message;
    public List<Transaction> result;


    public static class Transaction {
        public String value;
        public String blockNumber;
        public String confirmations;
        public String contractAddress;
        public String tokenSymbol;
        public String timestamp;
        public String hash;
        public String blockHash;
        public String from;
        public String to;
        public String tokenDecimal;
    }
}
