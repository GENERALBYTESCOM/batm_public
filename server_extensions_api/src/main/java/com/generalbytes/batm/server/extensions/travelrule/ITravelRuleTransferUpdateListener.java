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
 * Represents a listener for transfer status updates.
 *
 * <p>This listener can be registered to a Travel Rule Provider to receive updates on transfer statuses.</p>
 *
 * @see ITravelRuleProvider#registerStatusUpdateListener(ITravelRuleTransferUpdateListener)
 */
@FunctionalInterface
public interface ITravelRuleTransferUpdateListener {

    /**
     * Call this method whenever a transfer changes status to one that can be represented with {@link TravelRuleProviderTransferStatus}.
     *
     * <p>The server provides an implementation for this interface and further processing of transfers
     * depends on this method being called when a transfer is eventually Approved or Rejected.</p>
     *
     * @param event Event holding information about the change.
     * @throws IllegalArgumentException If transferPublicId or newTransferStatus in the event is null or invalid.
     */
    void onTransferStatusUpdate(ITravelRuleTransferStatusUpdateEvent event);

}