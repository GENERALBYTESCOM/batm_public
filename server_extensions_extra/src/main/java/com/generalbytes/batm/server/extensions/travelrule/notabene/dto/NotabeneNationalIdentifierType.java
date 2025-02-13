package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

/**
 * Identifies the national identification type.
 */
public enum NotabeneNationalIdentifierType {
    /**
     * Alien registration number - Number assigned by a government agency to identify foreign nationals.
     */
    ARNU,
    /**
     * Passport number - Number assigned by a passport authority.
     */
    CCPT,
    /**
     * Registration authority identifier - Identifier of a legal entity as maintained by a registration authority.
     */
    RAID,
    /**
     * Driver license number - Number assigned to a driver's license.
     */
    DRLC,
    /**
     * Foreign investment identity number - Number assigned to a foreign investor (other than the alien number).
     */
    FIIN,
    /**
     * Tax identification number - Number assigned by a tax authority to an entity.
     */
    TXID,
    /**
     * Social security number - Number assigned by a social security agency.
     */
    SOCS,
    /**
     * Identity card number - Number assigned by a national authority to an identity card.
     */
    IDCD,
    /**
     * Legal Entity Identifier - Legal Entity Identifier (LEI) assigned in accordance with ISO 17442 11 .
     */
    LEIX,
    /**
     * Unspecified - A national identifier which may be known but which cannot otherwise be categorized
     * or the category of which the sender is unable to determine.
     */
    MISC
}
