package com.generalbytes.batm.server.extensions.extra.ethereum.etherscan;

import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.RestProxyFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public interface IEtherscanAPI {

    static IEtherscanAPI create(String apiKey) {
        ClientConfig config = new ClientConfig();
        config.addDefaultParam(PathParam.class, "apikey", apiKey);
        return RestProxyFactory.createProxy(IEtherscanAPI.class, "https://api.etherscan.io", config);
    }

    @GET
    @Path("/api?module=account&action=tokentx&address={address}&apikey={apikey}")
    GetTransactionsResponse getTokenTransactions(@PathParam("address") String address) throws EtherScanException, IOException;

    @GET
    @Path("/api?module=account&action=txlist&address={address}&apikey={apikey}")
    GetTransactionsResponse getTransactions(@PathParam("address") String address) throws EtherScanException, IOException;

}
