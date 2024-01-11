package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto;
import com.google.gson.annotations.SerializedName;

public class TimeBounds {
    @SerializedName("min_time")
    private long minTime;

    @SerializedName("max_time")
    private long maxTime;

    // Add getters and setters as needed

    @Override
    public String toString() {
        return "TimeBounds{" +
                "minTime=" + minTime +
                ", maxTime=" + maxTime +
                '}';
    }
}
