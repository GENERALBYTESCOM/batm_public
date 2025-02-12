package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request to create a new transfer.
 *
 * <p>This is also used to validate a transfer before creating it.</p>
 *
 * @see <a href="https://devx.notabene.id/reference/txcreate-1">Notabene Create Documentation</a>
 * @see <a href="https://devx.notabene.id/reference/txvalidatefull-1">Notabene Validate Documentation</a>
 * @see NotabeneTransferInfo
 * @see NotabeneFullyValidateTransferResponse
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class NotabeneTransferCreateRequest {

    /**
     * Idempotency key: to prevent duplicate transactions.
     * It is highly recommended to populate this field with a unique identifier for the transfer.
     */
    private String transactionRef;
    /**
     * The type of the asset as Asset Symbol (BTC, ETH)
     */
    private String transactionAsset;
    /**
     * Amount in base unit of the asset (satoshi, wei, etc.)
     */
    private String transactionAmount;
    /**
     * Originator VASP Decentralized Identifier
     *
     * @see <a href="https://devx.notabene.id/docs/decentralized-identifiers-dids">Notabene Documentation</a>
     */
    @JsonProperty("originatorVASPdid")
    private String originatorVaspDid;
    /**
     * Beneficiary VASP Decentralized Identifier
     *
     * @see <a href="https://devx.notabene.id/docs/decentralized-identifiers-dids">Notabene Documentation</a>
     */
    @JsonProperty("beneficiaryVASPdid")
    private String beneficiaryVaspDid;
    /**
     * Information about the transaction on the blockchain
     *
     * @see NotabeneTransactionBlockchainInfo
     */
    private NotabeneTransactionBlockchainInfo transactionBlockchainInfo;
    private NotabeneOriginator originator;
    private NotabeneBeneficiary beneficiary;
    private NotabeneProof beneficiaryProof;

}