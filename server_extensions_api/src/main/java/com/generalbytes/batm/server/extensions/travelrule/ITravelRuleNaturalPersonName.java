/*************************************************************************************
 * Copyright (C) 2014-2025 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.travelrule;

/**
 * Holds the name data for a natural person.
 */
public interface ITravelRuleNaturalPersonName {

    /**
     * Get the primary name of the natural person.
     */
    String getPrimaryName();

    /**
     * Get the secondary name of the natural person.
     */
    String getSecondaryName();

    /**
     * Get the type of the name.
     *
     * <p>Possible Values:</p>
     * <ul>
     *     <li>{@code ALIA} - Alias name - A name other than the legal name by which a natural person is also known.</li>
     *     <li>{@code BIRT} - Name at birth - The name given to a natural person at birth.</li>
     *     <li>{@code MAID} - Maiden name - The original name of a natural person who has changed their name after marriage.</li>
     *     <li>{@code LEGL} - Legal name - The name that identifies a natural person for legal, official or administrative purposes.</li>
     *     <li>{@code MISC} - Unspecified - A name by which a natural person may be known but which cannot otherwise be categorized
     *     or the category of which the sender is unable to determine.</li>
     *     <li>{@code null}</li>
     * </ul>
     *
     * @return The type of the name. Can be null.
     */
    String getNameType();

}
