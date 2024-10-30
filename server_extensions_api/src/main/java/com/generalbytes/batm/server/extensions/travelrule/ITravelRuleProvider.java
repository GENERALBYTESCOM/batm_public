/*************************************************************************************
 * Copyright (C) 2014-2024 GENERAL BYTES s.r.o. All rights reserved.
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
 * A Travel Rule Provider definition that makes it possible to connect to an external provider using its API.
 * Provider is responsible for implementing compliance checks and procedures necessary to ensure adherence to the Travel Rule regulations.
 */
public interface ITravelRuleProvider {

    /**
     * This is used as the provider identifier that is displayed in CAS.
     * @return Name of Travel Rule Provider.
     */
    String getName();

    // TODO: implement methods

}
