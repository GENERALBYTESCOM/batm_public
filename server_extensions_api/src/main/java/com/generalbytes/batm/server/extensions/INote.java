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

import java.util.Date;

/**
 * Note entry related to some {@link IIdentity} or {@link ILocation}.
 */
public interface INote {

    /**
     * @return note ID
     */
    Long getId();

    /**
     * @return content of the note
     */
    String getText();

    /**
     * @return time of create the note
     */
    Date getCreatedAt();

    /**
     * @return time of delete the note
     */
    Date getDeletedAt();

    /**
     * @return name of the user who created the note
     */
    String getUserName();

    /**
     * @return true if note is marked as deleted
     */
    boolean isDeleted();

}
