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

/**
 * This class defines basic interface which every watchlist sould conform to
 */
public interface IWatchList {
    public static final int LIST_NOT_CHANGED    = 0;
    public static final int LIST_CHANGED        = 1;
    public static final int LIST_REFRESH_FAILED = 2;


    /**
     * Ths method is called just once and before refresh method is called
     * @param downloadDirectory
     */
    public void init(String downloadDirectory);

    /**
     * Unique name of the watchlist
     * @return
     */
    public String getName();

    /**
     * Short description of the watchlist. For instance link to a website containing more information about the watchlist data
     * @return
     */
    public String getDescription();

    /**
     * Performs the re-download of the watchlist from the remote side @see LIST_NOT_CHANGED or LIST_CHANGED or LIST_REFRESH_FAILED if download fails.
     * @return
     */
    public int refresh();

    /**
     * This method returns number of recommended minutes for which the watchlist is considered valid. After this period method refresh() should be called again.
     * @return
     */
    public int recommendedRefreshPeriodInMins();

    /**
     * This method is used to query the watchlist for results
     * @param query
     * @return
     */
    public WatchListResult search(WatchListQuery query);

}
