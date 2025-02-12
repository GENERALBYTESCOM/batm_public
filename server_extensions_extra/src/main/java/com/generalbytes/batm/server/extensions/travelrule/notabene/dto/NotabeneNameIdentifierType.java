package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

/**
 * Represents the nature of name being adopted.
 */
public enum NotabeneNameIdentifierType {
    /**
     * Alias name - A name other than the legal name by which a natural person is also known.
     */
    ALIA,
    /**
     * Name at birth - The name given to a natural person at birth.
     */
    BIRT,
    /**
     * Maiden name - The original name of a natural person who has changed their name after marriage.
     */
    MAID,
    /**
     * Legal name - The name that identifies a natural person for legal, official or administrative purposes.
     */
    LEGL,
    /**
     * Unspecified - A name by which a natural person may be known but which cannot otherwise be categorized
     * or the category of which the sender is unable to determine.
     */
    MISC
}
