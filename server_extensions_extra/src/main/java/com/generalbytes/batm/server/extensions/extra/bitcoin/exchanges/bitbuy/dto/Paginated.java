package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy.dto;

import java.util.List;

public class Paginated<T> {
    public List<T> result;

    public Integer pageNumber;
    public Integer totalPages;
    public Integer currentCount;
    public Integer totalCount;
    public Integer limit;
    public Boolean hasNextPage;
}
