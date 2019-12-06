/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used for publishing REST services on BATM CAS Server
 */
public interface IRestService {
    /**
     * Defines URL path to rest service.
     * If method returns value "myextension" it means that rest service will be accessible on
     * following https://localhost:7743/extensions/myextension
     * @return
     */
    String getPrefixPath();

    /**
     * Returns REST service implementation class that uses JSR-000311 JAX-RS
     * @return
     */
    Class getImplementation();

    /**
     * Returns list of filters used in this rest service.
     * Filter class has to implement javax.servlet.Filter interface.
     * @return
     */
    default List<Class> getFilters() {
        return new ArrayList<>(0);
    }
}
