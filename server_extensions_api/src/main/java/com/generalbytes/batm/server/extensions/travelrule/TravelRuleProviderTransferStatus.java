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
 * Represents possible statuses of a transfer.
 */
public enum TravelRuleProviderTransferStatus {
    /**
     * The transfer has been approved by the VASP. Transfer will be processed.
     */
    APPROVED,
    /**
     * The transfer has been rejected by the VASP, for any reason. Means transfer won't be processed.
     */
    REJECTED,
    /**
     * The transfer is still pending approval by the VASP. Initial state when transfer is created.
     */
    IN_PROGRESS
}
