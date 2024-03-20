package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class SiginEnvelopeResponse {
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getEnvelope() {
		return envelope;
	}

	public void setEnvelope(String envelope) {
		this.envelope = envelope;
	}

	public String getSignedEnvelope() {
		return signedEnvelope;
	}

	public void setSignedEnvelope(String signedEnvelope) {
		this.signedEnvelope = signedEnvelope;
	}

	@SerializedName("id")
    private String id;

    @SerializedName("created")
    private Date created;

    @SerializedName("modified")
    private Date modified;

    @SerializedName("status")
    private String status;

    @SerializedName("envelope")
    private String envelope;

    @SerializedName("signed_envelope")
    private String signedEnvelope;

    // Add getters and setters as needed

    @Override
    public String toString() {
        return "ApiResponse{" +
                "id='" + id + '\'' +
                ", created=" + created +
                ", modified=" + modified +
                ", status='" + status + '\'' +
                ", envelope='" + envelope + '\'' +
                ", signedEnvelope='" + signedEnvelope + '\'' +
                '}';
    }
}