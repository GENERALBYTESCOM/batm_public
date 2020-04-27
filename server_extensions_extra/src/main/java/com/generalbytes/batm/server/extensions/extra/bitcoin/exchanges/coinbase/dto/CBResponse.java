package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinbase.dto;

public class CBResponse {

    public CBError[] errors;
    @SuppressWarnings("WeakerAccess")
    public CBError[] warnings;

    public static class CBError {
        public String id;
        public String message;
        public String url;

        @Override
        public String toString() {
            return "CBError{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", url='" + url + '\'' +
                '}';
        }
    }

    public String getErrorMessages() {
        return toString(errors);
    }

    @SuppressWarnings("unused")
    public String getWarningMessages() {
        return toString(warnings);
    }

    private String toString(CBError[] values) {
        if (values != null && values.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (CBError v : values) {
                if (v.id != null) {
                    sb.append(sb.length() == 0 ? "" : ", ");
                    sb.append("id = ").append(v.id);
                }
                if (v.message != null) {
                    sb.append(sb.length() == 0 ? "" : ", ");
                    sb.append("message = ").append(v.message);
                }
                if (v.url != null) {
                    sb.append(sb.length() == 0 ? "" : ", ");
                    sb.append("url = ").append(v.url);
                }
            }
            return sb.toString();
        }
        return null;
    }

}
