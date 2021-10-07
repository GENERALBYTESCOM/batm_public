package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.bitbuy;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import si.mazi.rescu.RequestWriterResolver;
import si.mazi.rescu.RestInvocation;
import si.mazi.rescu.RestMethodMetadata;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class BitbuyDigestTest {

    private interface TestApi {
        @GET
        @Path("/test/path")
        String testGet(@QueryParam("param1") String param1, @QueryParam("param2") String param2) throws IOException;

        @POST
        @Path("/test/path")
        @Consumes(MediaType.APPLICATION_JSON)
        String testPost(@QueryParam("param1") String param1, @QueryParam("param2") String param2, String body) throws IOException;
    }

    @Test
    public void testGetMacDataGet() throws GeneralSecurityException, NoSuchMethodException {
        Method method = TestApi.class.getMethod("testGet", String.class, String.class);
        String expected = "{\"path\":\"interfacepath/test/path\",\"content-length\":-1,\"query\":\"param1=param1value&param2=param2value\"}";
        Assert.assertEquals(expected, getMacData(method, "param1value", "param2value"));
    }

    @Test
    public void testGetMacDataPost() throws GeneralSecurityException, NoSuchMethodException {
        Method method = TestApi.class.getMethod("testPost", String.class, String.class, String.class);
        String expected = "{\"path\":\"interfacepath/test/path\",\"content-length\":6,\"query\":\"param1=param1value&param2=param2value\"}";
        Assert.assertEquals(expected, getMacData(method, "param1value", "param2value", "body"));
    }

    private String getMacData(Method method, Object... args) throws GeneralSecurityException {
        RestMethodMetadata methodMetadata = RestMethodMetadata.create(method, "https://baseurl/", "interfacepath");
        RequestWriterResolver requestWriterResolver = RequestWriterResolver.createDefault(new ObjectMapper());
        RestInvocation restInvocation = RestInvocation.create(requestWriterResolver, methodMetadata, args, null);

        BitbuyDigest digest = new BitbuyDigest("secret");
        return new String(digest.getMacData(restInvocation), StandardCharsets.UTF_8);
    }
}