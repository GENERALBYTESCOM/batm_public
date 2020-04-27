package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

import java.util.List;

/**
 * Created by b00lean on 23.7.17.
 */

public class CBResponse {
    private List<CBError> errors;
    private List<CBWarning> warnings;

    public List<CBError> getErrors() {
        return errors;
    }

    public void setErrors(List<CBError> errors) {
        this.errors = errors;
    }

    public List<CBWarning> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<CBWarning> warnings) {
        this.warnings = warnings;
    }

    public String getErrorMessages() {
        return toString(errors);
    }

    private String toString(List<CBError> errors) {
        if (errors != null && errors.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (CBError v : errors) {
                if (v.getId() != null) {
                    sb.append(sb.length() == 0 ? "" : ", ");
                    sb.append("id = ").append(v.getId());
                }
                if (v.getMessage() != null) {
                    sb.append(sb.length() == 0 ? "" : ", ");
                    sb.append("message = ").append(v.getMessage());
                }
            }
            return sb.toString();
        }
        return null;
    }

}
