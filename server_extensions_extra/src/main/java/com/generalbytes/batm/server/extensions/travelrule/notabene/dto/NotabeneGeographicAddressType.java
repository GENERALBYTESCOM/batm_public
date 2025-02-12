package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

/**
 * Identifies the nature of the address.
 */
public enum NotabeneGeographicAddressType {
    /**
     * Residential - Address is the home address.
     */
    HOME,
    /**
     * Business - Address is the business address.
     */
    BIZZ,
    /**
     * Geographic - Address is the unspecified physical (geographical) address
     * suitable for identification of the natural or legal person.
     */
    GEOG
}
