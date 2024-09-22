package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto;
import com.google.gson.annotations.SerializedName;

public class Link {
    @SerializedName("href")
    private String href;

    // Add getters and setters as needed

    @Override
    public String toString() {
        return "Link{" +
                "href='" + href + '\'' +
                '}';
    }
}
