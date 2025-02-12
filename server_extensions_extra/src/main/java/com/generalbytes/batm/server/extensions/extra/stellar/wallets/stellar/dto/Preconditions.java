package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto;
import com.google.gson.annotations.SerializedName;

public class Preconditions {
    @SerializedName("timebounds")
    private TimeBounds timeBounds;

    // Add getters and setters as needed

    @Override
    public String toString() {
        return "Preconditions{" +
                "timeBounds=" + timeBounds +
                '}';
    }
}
