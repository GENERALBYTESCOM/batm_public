package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneApi;
import com.generalbytes.batm.server.extensions.travelrule.notabene.api.NotabeneAuthApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import si.mazi.rescu.RestProxyFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotabeneApiFactoryTest {

    private static final String NOTABENE_API_URL = "https://api.notabene.id";
    private static final String NOTABENE_AUTH_API_URL = "https://auth.notabene.id";

    @Mock
    private NotabeneConfiguration configuration;
    @InjectMocks
    private NotabeneApiFactory notabeneApiFactory;

    @Test
    void testGetNotabeneApi() {
        NotabeneApi notabeneApiMock = mock(NotabeneApi.class);

        try (MockedStatic<RestProxyFactory> mockedRestProxyFactory = mockStatic(RestProxyFactory.class)) {
            when(configuration.getApiUrl()).thenReturn(NOTABENE_API_URL);
            mockedRestProxyFactory.when(() -> RestProxyFactory.createProxy(NotabeneApi.class, NOTABENE_API_URL))
                .thenReturn(notabeneApiMock);

            NotabeneApi result = notabeneApiFactory.getNotabeneApi();

            assertEquals(notabeneApiMock, result);
            mockedRestProxyFactory.verify(() -> RestProxyFactory.createProxy(NotabeneApi.class, NOTABENE_API_URL), times(1));
        }
    }

    @Test
    void testGetNotabeneAuthApi() {
        NotabeneAuthApi notabeneAuthApiMock = mock(NotabeneAuthApi.class);

        try (MockedStatic<RestProxyFactory> mockedRestProxyFactory = mockStatic(RestProxyFactory.class)) {
            when(configuration.getAuthApiUrl()).thenReturn(NOTABENE_AUTH_API_URL);
            mockedRestProxyFactory.when(() -> RestProxyFactory.createProxy(NotabeneAuthApi.class, NOTABENE_AUTH_API_URL))
                .thenReturn(notabeneAuthApiMock);

            NotabeneAuthApi result = notabeneApiFactory.getNotabeneAuthApi();

            assertEquals(notabeneAuthApiMock, result);
            mockedRestProxyFactory.verify(() -> RestProxyFactory.createProxy(NotabeneAuthApi.class, NOTABENE_AUTH_API_URL), times(1));
        }
    }

}