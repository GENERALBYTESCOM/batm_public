package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto;

import java.util.List;

/**
 * Created by b00lean on 23.7.17.
 */

public class CBAccountsResponse extends CBResponse{
    private CBPagination pagination;
    private List<CBAccount> data;

    public CBPagination getPagination() {
        return pagination;
    }

    public void setPagination(CBPagination pagination) {
        this.pagination = pagination;
    }

    public List<CBAccount> getData() {
        return data;
    }

    public void setData(List<CBAccount> data) {
        this.data = data;
    }
}
