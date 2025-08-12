/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
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
package com.generalbytes.batm.server.extensions.extra.bitcoin.sources.coinpaprika;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

// https://api.coinpaprika.com/
@Produces(MediaType.APPLICATION_JSON)
@Path("/v1")
public interface CoinPaprikaV1API {
    @GET
    @Path("/tickers/{coin_id}")
    CoinPaprikaTickerResponse getTicker(@PathParam("coin_id") String coinId, @QueryParam("quotes") String quotes) throws IOException;

    /**
     * Get historical tickers for specific coin
     * @param coinId Example: btc-bitcoin
     * @param start start point for historical data.
     *              Supported formats:
     *              <ul>
     *                  <li>RFC3999 (ISO-8601) eg. <code>2018-02-15T05:15:00Z</code></li>
     *                  <li>Simple date (yyyy-mm-dd) eg. <code>2018-02-15</code></li>
     *                  <li>Unix timestamp (in seconds) eg. <code>1518671700</code></li>
     *              </ul>
     *              <br/>
     *
     *
     *
     * @param end end point for historical data.
     *            Supported formats:
     *            <ul>
     *                <li>RFC3999 (ISO-8601) eg. <code>2018-02-15T05:15:00Z</code></li>
     *                <li>Simple date (yyyy-mm-dd) eg. <code>2018-02-15</code></li>
     *                <li>Unix timestamp (in seconds) eg. <code>1518671700</code></li>
     *                <li>value <code>NOW</code></li>
     *            </ul>
     *            <br/>
     *
     * @param limit limit of result rows (max 5000)
     * @param quote returned data quote (available values: <code>usd</code>, <code>btc</code>)
     * @param interval returned points interval (available values: 5m, 10m, 15m, 30m, 45m, 1h, 2h, 3h, 6h, 12h, 24h, 1d,
     *                7d, 14d, 30d, 90d, 365d)
     * @return
     * @throws IOException
     */
    @GET
    @Path("/tickers/{coin_id}/historical")
    List<CoinPaprikaHistoricalTickerResponse> getHistorical(
        @PathParam("coin_id") String coinId,
        @QueryParam("start") String start,
        @QueryParam("end") String end,
        @QueryParam("limit") int limit,
        @QueryParam("quote") String quote,
        @QueryParam("interval") String interval
    ) throws IOException;

    @GET
    @Path("/global")
    CoinPaprikaGlobalResponse getGlobal() throws IOException;
}
