package com.generalbytes.batm.server.extensions.extra.identityverification.identitypiece;

import com.generalbytes.batm.server.extensions.IIdentityPiece;
import com.generalbytes.batm.server.extensions.IPerson;
import com.generalbytes.batm.server.extensions.IdScanDocumentType;

import java.util.Date;
import java.util.UUID;

public abstract class DataIdentityPiece implements IIdentityPiece {

    private final String mime;
    private final byte[] data;
    private UUID correlationId;

    protected DataIdentityPiece(String mime, byte[] data) {
        this.data = data;
        this.mime = mime;
    }

    @Override
    public Date getCreated() {
        return null;
    }

    @Override
    public String getPhoneNumber() {
        return null;
    }

    @Override
    public String getFilename() {
        return null;
    }


    @Override
    public String getEmailAddress() {
        return null;
    }

    @Override
    public String getFirstname() {
        return null;
    }

    @Override
    public String getLastname() {
        return null;
    }

    @Override
    public String getContactAddress() {
        return null;
    }

    @Override
    public String getContactCity() {
        return null;
    }

    @Override
    public String getContactCountry() {
        return null;
    }

    @Override
    public String getContactCountryIso2() {
        return null;
    }

    @Override
    public String getContactProvince() {
        return null;
    }

    @Override
    public String getContactZIP() {
        return null;
    }

    @Override
    public String getIssuingJurisdictionCountry() {
        return null;
    }

    @Override
    public String getIssuingJurisdictionProvince() {
        return null;
    }

    @Override
    public String getIdCardNumber() {
        return null;
    }

    @Override
    public IPerson getCreatedBy() {
        return null;
    }

    @Override
    public Date getDocumentValidTo() {
        return null;
    }

    @Override
    public Integer getDocumentType() {
        return null;
    }

    @Override
    public Date getDateOfBirth() {
        return null;
    }

    @Override
    public String getOccupation() {
        return null;
    }

    @Override
    public String getSSN() {
        return null;
    }

    @Override
    public IdScanDocumentType getIdScanDocumentType() {
        return null;
    }


    @Override
    public String getMimeType() {
        return mime;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public UUID getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(UUID correlationId) {
        this.correlationId = correlationId;
    }
}
