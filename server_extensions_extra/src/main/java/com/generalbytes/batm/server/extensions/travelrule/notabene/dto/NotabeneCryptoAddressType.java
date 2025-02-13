package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

/**
 * Represents the type of crypto address.
 */
public enum NotabeneCryptoAddressType {
    /**
     * Address is not known.
     */
    UNKNOWN,
    /**
     * Address is hosted by a custodial service.
     */
    HOSTED,
    /**
     * Address is unhosted.
     */
    UNHOSTED
}
