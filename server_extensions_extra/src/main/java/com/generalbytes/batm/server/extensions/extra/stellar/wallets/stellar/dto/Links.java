package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto;
import com.google.gson.annotations.SerializedName;

public class Links {
    @SerializedName("self")
    private Link self;

    @SerializedName("account")
    private Link account;

    @SerializedName("ledger")
    private Link ledger;

    @SerializedName("operations")
    private Link operations;

    @SerializedName("effects")
    private Link effects;

    @SerializedName("precedes")
    private Link precedes;

    @SerializedName("succeeds")
    private Link succeeds;

    @SerializedName("transaction")
    private Link transaction;

    // Add getters and setters as needed

    @Override
    public String toString() {
        return "Links{" +
                "self=" + self +
                ", account=" + account +
                ", ledger=" + ledger +
                ", operations=" + operations +
                ", effects=" + effects +
                ", precedes=" + precedes +
                ", succeeds=" + succeeds +
                ", transaction=" + transaction +
                '}';
    }
}
