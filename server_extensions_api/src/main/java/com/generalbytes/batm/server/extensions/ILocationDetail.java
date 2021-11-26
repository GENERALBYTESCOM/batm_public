/*************************************************************************************
 * Copyright (C) 2014-2021 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions;

import java.util.List;

/**
 * Extends ILocation by the remaining attributes to cover the complete functionality of Location.
 */
public interface ILocationDetail extends ILocation {

    IPerson getContactPerson();

    /**
     * The organization to which the location belongs.
     */
    IOrganization getOrganization();

    String getCashCollectionCompany();

    /**
     * @return - list of location notes
     */
    List<INote> getNotes();

    /**
     * TerminalCapacity represents how many terminals are allowed to be deployed to this location at the same time.
     * Locations without free capacity are not shown in terminal location selections.
     */
    Integer getTerminalCapacity();

    /**
     * @return - Records about openening hours. Each record represents one day of the week.
     */
    List<IOpeningHours> getOpeningHours();

    /**
     * @return Records about scheduled cash withdrawals. Each record represents one day of the month.
     */
    List<ICashCollectionDay> getCashCollectionDays();
}
