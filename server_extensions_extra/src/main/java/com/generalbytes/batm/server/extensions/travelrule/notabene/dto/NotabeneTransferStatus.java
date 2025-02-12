package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

/**
 * Status of a Transfer.
 *
 * @see <a href="https://devx.notabene.id/docs/tr-status_vnext">Notabene Documentation</a>
 */
public enum NotabeneTransferStatus {
    /**
     * Message has been created and is ready to be sent.
     */
    NEW,
    /**
     * Message is missing information about the beneficiary (only if txCreate is called
     * with the option skipBeneficiaryDataValidation=true).
     */
    MISSING_BENEFICIARY_DATA,
    /**
     * Missing contact details to reach the beneficiary VASP. Notabene to action.
     */
    WAITING_FOR_INFORMATION,
    /**
     * Message has been automatically or manually cancelled.
     */
    CANCELLED,
    /**
     * Message has been created with txNotify and is missing both originator and beneficiary information.
     *
     * <p>Transfer created only with Blockchain Data (called by Beneficiary).</p>
     */
    INCOMPLETE,
    /**
     * Message has been sent to the beneficiary VASP.
     */
    SENT,
    /**
     * Beneficiary VASP has confirmed they control the destination address.
     */
    ACK,
    /**
     * Beneficiary VASP accepted your value transfer request.
     */
    ACCEPTED,
    /**
     * Beneficiary VASP declined your value transfer request.
     */
    DECLINED,
    /**
     * Beneficiary VASP does not control the destination address.
     */
    REJECTED,
    /**
     * Beneficiary VASP is not ready to respond to travel rule messages.
     */
    NOT_READY,
    /**
     * The transaction is "BELOW_THRESHOLD," "NON_CUSTODIAL" or an internal transfer.
     * Therefore, after being created, it is not transmitted, just saved.
     */
    SAVED
}
