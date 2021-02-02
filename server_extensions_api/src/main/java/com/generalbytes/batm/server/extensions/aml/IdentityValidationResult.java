package com.generalbytes.batm.server.extensions.aml;

import java.io.Serializable;

public class IdentityValidationResult implements IIdentityValidationResult, Serializable {

    private String uid;
    private double score;
    private String description;

    public IdentityValidationResult() {
    }

    public IdentityValidationResult(String uid, double score, String description) {
        this.uid = uid;
        this.score = score;
        this.description = description;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getScore() {
        return score;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
