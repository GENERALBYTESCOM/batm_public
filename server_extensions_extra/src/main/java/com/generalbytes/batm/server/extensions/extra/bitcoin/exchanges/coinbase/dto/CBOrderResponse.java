package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto;

public class CBOrderResponse extends CBResponse {

    public CBOrder data;

    public static class CBOrder {

        public String id;
        public String status;
        public CBResource payment_method;
        public CBResource transaction;
        public CBAmount amount;
        public CBAmount total;
        public CBAmount subtotal;
        public String created_at;
        public String updated_at;
        public String resource;
        public String resource_path;
        public boolean committed;
        public boolean instant;
        public CBAmount fee;
        public String payout_at;
    }
}
