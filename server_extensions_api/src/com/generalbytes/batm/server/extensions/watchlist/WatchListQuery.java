/*************************************************************************************
 * Copyright (C) 2014-2016 GENERAL BYTES s.r.o. All rights reserved.
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

package com.generalbytes.batm.server.extensions.watchlist;

import java.io.Serializable;


public class WatchListQuery implements Serializable{
    public static final int TYPE_INDIVIDUAL = 0;
    public static final int TYPE_ENTITY = 1;

    private int type = TYPE_INDIVIDUAL;
    private String name;
    private String firstName;
    private String lastName;


    public WatchListQuery(String name) {
        this.type = TYPE_ENTITY;
        this.name = name;
    }

    public WatchListQuery(String firstName, String lastName) {
        this.type = TYPE_INDIVIDUAL;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }
}
