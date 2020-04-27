package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto;

import java.util.Arrays;

public class CBPaymentMethodsResponse extends CBResponse {

    public CBPaymentMethod[] data;

    public static class CBPaymentMethod {
        public String id;
        public String type;
        public String name;
        public String currency;

        @Override
        public String toString() {
            return "CBPaymentMethod{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", currency='" + currency + '\'' +
                '}';
        }
    }

    @Override
    public String toString() {
        return "CBPaymentMethodsResponse{" +
            "data=" + Arrays.toString(data) +
            ", errors=" + Arrays.toString(errors) +
            ", warnings=" + Arrays.toString(warnings) +
            '}';
    }
}
