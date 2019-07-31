/* ##
# Part of the Aquanow API price extension
#
* This software may be distributed and modified under the terms of the GNU
* General Public License version 2 (GPL2) as published by the Free Software
* Foundation and appearing in the file GPL2.TXT included in the packaging of
* this file. Please note that GPL2 Section 2[b] requires that all works based
* on this software must also be made publicly available under the terms of
* the GPL2 ("Copyleft").
#
## */

package com.generalbytes.batm.server.extensions.extra.aquanow.sources.aquanow;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;


@Path("/v1/")
@Produces(MediaType.APPLICATION_JSON)
public interface IAquanowAPI {

    @GET
    @Path("bestprice")
    @JsonProperty("bestAsk")

    public String getPrice(@QueryParam("symbol")  String symbol) throws IOException;
}
