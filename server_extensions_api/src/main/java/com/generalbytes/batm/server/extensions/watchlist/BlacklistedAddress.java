package com.generalbytes.batm.server.extensions.watchlist;

public class BlacklistedAddress {

    private String watchlistId;
    private String address;

    public BlacklistedAddress(String address) {
        this.address = address;
    }

    public BlacklistedAddress(String watchlistId, String address) {
        this.watchlistId = watchlistId;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWatchlistId() {
        return watchlistId;
    }

    public void setWatchlistId(String watchlistId) {
        this.watchlistId = watchlistId;
    }

    @Override
    public String toString() {
        return "BlacklistedAddress{" +
            "watchlistId='" + watchlistId + '\'' +
            ", address='" + address + '\'' +
            '}';
    }
}
