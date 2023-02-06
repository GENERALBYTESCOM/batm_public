package com.generalbytes.batm.server.extensions.aml.verification;

import com.generalbytes.batm.server.extensions.IIdentity;
import com.generalbytes.batm.server.extensions.IOrganization;

import java.util.Date;

/**
 * Applicant for Identity Verification.
 * Represents a single started attempt to verify an identity with an Identity Verification Provider.
 */
public class IdentityApplicant {

    private String applicantId;

    private String verificationWebUrl;

    private Date dateCreated = new Date();

    /**
     * only present when verification provider is called from the same server this identity belongs to
     */
    private IIdentity identity;
    /**
     * Identity's organization, present even when identity is null (on cloud, when identity is on a different standalone server)
     */
    private IOrganization organization;

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

    public IOrganization getOrganization() {
        return organization;
    }

    public void setOrganization(IOrganization organization) {
        this.organization = organization;
    }
}
