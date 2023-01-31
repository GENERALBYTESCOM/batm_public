package com.generalbytes.batm.server.extensions.aml.verification;

import com.generalbytes.batm.server.extensions.IIdentity;

import java.util.Date;

/**
 * Applicant for Identity Verification.
 * Represents a single started attempt to verify an identity with an Identity Verification Provider.
 */
public class IdentityApplicant {

    private String applicantId;

    private String verificationWebUrl;

    private Date dateCreated = new Date();

    private IIdentity identity;

    public IdentityApplicant() {
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getVerificationWebUrl() {
        return verificationWebUrl;
    }

    public void setVerificationWebUrl(String verificationWebUrl) {
        this.verificationWebUrl = verificationWebUrl;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public IIdentity getIdentity() {
        return identity;
    }

    public void setIdentity(IIdentity identity) {
        this.identity = identity;
    }

}
