package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class TransactionResponse {
	@SerializedName("memo")
	private String memo;

	@SerializedName("memo_bytes")
	private String memoBytes;

	@SerializedName("_links")
	private Links links;

	@SerializedName("id")
	private String id;

	@SerializedName("paging_token")
	private String pagingToken;

	@SerializedName("successful")
	private boolean successful;

	@SerializedName("hash")
	private String hash;

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@SerializedName("ledger")
	private int ledger;

	@SerializedName("created_at")
	private Date createdAt;

	@SerializedName("source_account")
	private String sourceAccount;

	@SerializedName("source_account_sequence")
	private String sourceAccountSequence;

	@SerializedName("fee_account")
	private String feeAccount;

	@SerializedName("fee_charged")
	private String feeCharged;

	@SerializedName("max_fee")
	private String maxFee;

	@SerializedName("operation_count")
	private int operationCount;

	@SerializedName("envelope_xdr")
	private String envelopeXdr;

	@SerializedName("result_xdr")
	private String resultXdr;

	@SerializedName("result_meta_xdr")
	private String resultMetaXdr;

	@SerializedName("fee_meta_xdr")
	private String feeMetaXdr;

	@SerializedName("memo_type")
	private String memoType;

	@SerializedName("signatures")
	private List<String> signatures;

	@SerializedName("valid_after")
	private Date validAfter;

	@SerializedName("valid_before")
	private Date validBefore;

	@SerializedName("preconditions")
	private Preconditions preconditions;

	// Add getters and setters as needed

	@Override
	public String toString() {
		return "HorizonResponse{" + "memo='" + memo + '\'' + ", memoBytes='" + memoBytes + '\'' + ", links=" + links
				+ ", id='" + id + '\'' + ", pagingToken='" + pagingToken + '\'' + ", successful=" + successful
				+ ", hash='" + hash + '\'' + ", ledger=" + ledger + ", createdAt=" + createdAt + ", sourceAccount='"
				+ sourceAccount + '\'' + ", sourceAccountSequence='" + sourceAccountSequence + '\'' + ", feeAccount='"
				+ feeAccount + '\'' + ", feeCharged='" + feeCharged + '\'' + ", maxFee='" + maxFee + '\''
				+ ", operationCount=" + operationCount + ", envelopeXdr='" + envelopeXdr + '\'' + ", resultXdr='"
				+ resultXdr + '\'' + ", resultMetaXdr='" + resultMetaXdr + '\'' + ", feeMetaXdr='" + feeMetaXdr + '\''
				+ ", memoType='" + memoType + '\'' + ", signatures=" + signatures + ", validAfter=" + validAfter
				+ ", validBefore=" + validBefore + ", preconditions=" + preconditions + '}';
	}
}
