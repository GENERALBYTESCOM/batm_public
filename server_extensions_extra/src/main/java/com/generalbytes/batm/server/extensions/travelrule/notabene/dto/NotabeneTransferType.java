package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

/**
 * Holds all possible types of a transfer.
 */
public enum NotabeneTransferType {
    /**
     * Transfer is below all threshold.
     */
    BELOW_THRESHOLD,
    /**
     * Transfer is on a customer owned wallet. Proof required.
     */
    NON_CUSTODIAL,
    /**
     * Transfer is between VASPs. TravelRule transfer created.
     */
    TRAVELRULE,
    /**
     * Transfer with any missing information.
     */
    UNKNOWN
}
