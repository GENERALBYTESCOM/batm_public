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
package com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.v2;

import com.generalbytes.batm.server.extensions.extra.dash.sources.coinmarketcap.CMCTicker;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/v2")
@Produces(MediaType.APPLICATION_JSON)
public interface ICoinmarketcapV2API {

    /**
     * Method getAllTickers() returns tickers for all supported crypto currencies.
     *
     * @return Map of all tickers for all supported crypto currencies
     */
    @GET
    @Path("/ticker")
    Map<String, Object> getAllTickers();

    /**
     * Method getTicker() returns ticker and has only one parameter - id of type String,
     * and the method uses default fiat currency which is 'USD'
     *
     * @param id of type String
     * @return
     */
    @GET
    @Path("/ticker/{id}")
    Map<String, Map<String, Object>> getTicker(@PathParam("id") String id);

    /**
     * Method getTicker() returns ticker and has only one parameter - id of type Integer,
     * and the method uses default fiat currency which is 'USD'
     *
     * @param id of type Integer
     * @return
     */
    @GET
    @Path("/ticker/{id}")
    Map<String, Map<String, Object>> getTicker(@PathParam("id") Integer id);

    /**
     * Method getTicker() has two parameters - id of type String, and fiat currency of type String in format 'XXX'
     * like for example 'EUR'
     *
     * @param id - String
     * @param fiatCurrency - String, for example EUR
     * @return
     */
    @GET
    @Path("/ticker/{id}")
    Map<String, Map<String, Object>> getTicker(@PathParam("id") String id, @QueryParam("convert") String fiatCurrency);

    /**
     * Method getTicker() has two parameters - id of type Integer, and fiat currency of type String in format 'XXX'
     * like for example 'EUR'
     *
     * @param id - Integer
     * @param fiatCurrency - String, for example 'EUR'
     * @return
     */
    @GET
    @Path("/ticker/{id}/")
    Map<String, Map<String, Object>>  getTicker(@PathParam("id") Integer id, @QueryParam("convert") String fiatCurrency);

    /**
     * Method getListings() returns map which contain all suported crypto currencies.
     * For all cryptocurrencies there is associated id.
     *
     * @return Map of cryptocurrencies
     */
    @GET
    @Path("/listings")
    Map<String, Object> getListings();

    /**
     * Method global returns a map which contains data like active cryptocurrencies, active crypto markets and etc.
     * @return map which contains global data about all cryptocurrencies available
     */
    @GET
    @Path("/global")
    Map<String, Object> getGlobal();

}