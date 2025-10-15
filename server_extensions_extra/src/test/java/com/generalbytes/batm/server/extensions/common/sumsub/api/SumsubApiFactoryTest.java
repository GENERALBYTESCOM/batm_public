package com.generalbytes.batm.server.extensions.common.sumsub.api;

import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.ISumSubApi;
import com.generalbytes.batm.server.extensions.travelrule.sumsub.api.SumsubTravelRuleApi;
import com.generalbytes.batm.server.extensions.common.sumsub.api.digest.SumsubSignatureDigest;
import com.generalbytes.batm.server.extensions.common.sumsub.api.digest.SumsubTimestampProvider;
import jakarta.ws.rs.HeaderParam;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.Params;
import si.mazi.rescu.RestProxyFactory;
import si.mazi.rescu.clients.HttpConnectionType;

import java.lang.annotation.Annotation;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

class SumsubApiFactoryTest {

    private final SumsubApiFactory apiFactory = new SumsubApiFactory();

    @Test
    void testCreateSumsubIdentityVerificationApi() {
        try (MockedStatic<RestProxyFactory> restProxyFactoryMock = mockStatic(RestProxyFactory.class)) {
            ISumSubApi mockApi = mock(ISumSubApi.class);

            restProxyFactoryMock.when(() -> RestProxyFactory.createProxy(
                eq(ISumSubApi.class), eq("https://api.sumsub.com"), any(ClientConfig.class)
            )).thenReturn(mockApi);

            ISumSubApi api = apiFactory.createSumsubIdentityVerificationApi("token", "secret");

            assertSame(mockApi, api);

            ArgumentCaptor<ClientConfig> clientConfigCaptor = ArgumentCaptor.forClass(ClientConfig.class);
            restProxyFactoryMock.verify(() -> RestProxyFactory.createProxy(
                eq(ISumSubApi.class), eq("https://api.sumsub.com"), clientConfigCaptor.capture()
            ));

            ClientConfig clientConfig = clientConfigCaptor.getValue();
            assertInstanceOf(CustomObjectMapperFactory.class, clientConfig.getJacksonObjectMapperFactory());
            assertEquals(HttpConnectionType.java, clientConfig.getConnectionType());

            assertDefaultParams(clientConfig);
        }
    }

    @Test
    void testCreateSumsubTravelRuleApi() {
        try (MockedStatic<RestProxyFactory> restProxyFactoryMock = mockStatic(RestProxyFactory.class)) {
            SumsubTravelRuleApi mockApi = mock(SumsubTravelRuleApi.class);

            restProxyFactoryMock.when(() -> RestProxyFactory.createProxy(
                    eq(SumsubTravelRuleApi.class), eq("https://api.sumsub.com"), any(ClientConfig.class)
            )).thenReturn(mockApi);

            SumsubTravelRuleApi api = apiFactory.createSumsubTravelRuleApi("token", "secret");

            assertSame(mockApi, api);

            ArgumentCaptor<ClientConfig> clientConfigCaptor = ArgumentCaptor.forClass(ClientConfig.class);
            restProxyFactoryMock.verify(() -> RestProxyFactory.createProxy(
                    eq(SumsubTravelRuleApi.class), eq("https://api.sumsub.com"), clientConfigCaptor.capture()
            ));

            ClientConfig clientConfig = clientConfigCaptor.getValue();
            assertInstanceOf(CustomObjectMapperFactory.class, clientConfig.getJacksonObjectMapperFactory());
            assertEquals(HttpConnectionType.apache, clientConfig.getConnectionType());

            assertDefaultParams(clientConfig);
        }
    }

    private void assertDefaultParams(ClientConfig clientConfig) {
        Map<Class<? extends Annotation>, Params> defaultParams = clientConfig.getDefaultParamsMap();
        Params params = defaultParams.get(HeaderParam.class);

        assertEquals("token", params.getParamValue("X-App-Token"));
        assertInstanceOf(SumsubTimestampProvider.class, params.getParamValue("X-App-Access-Ts"));
        assertInstanceOf(SumsubSignatureDigest.class, params.getParamValue("X-App-Access-Sig"));
    }

}