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
 * This event is responsible for holding information about a transfer status change.
 *
 * <p>This is passed to {@link ITravelRuleTransferUpdateListener} whenever a transfer changes status.</p>
 */
public interface ITravelRuleTransferStatusUpdateEvent {

    /**
     * Get the public id of the affected transfer.
     *
     * <p>This is the id provided by server at create.</p>
     *
     * @return The public id.
     * @see ITravelRuleTransferData#getPublicId()
     */
    String getTransferPublicId();

    /**
     * Get the new status of the updated transfer.
     *
     * @return The new status.
     */
    TravelRuleProviderTransferStatus getNewTransferStatus();

}
