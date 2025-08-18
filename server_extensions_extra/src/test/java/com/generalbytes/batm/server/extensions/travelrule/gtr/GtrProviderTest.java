package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.CryptoWalletType;
import com.generalbytes.batm.server.extensions.travelrule.IIdentityWalletEvaluationRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferListener;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferResolvedEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleVasp;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleWalletInfo;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVaspBasicInfo;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrTransferHandler;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrVerifyPiiWebhookHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrProviderTest {

    @Mock
    private GtrConfiguration configuration;
    @Mock
    private GtrService gtrService;
    @Mock
    private GtrTransferHandler transferHandler;
    @Mock
    private GtrVerifyPiiWebhookHandler verifyPiiWebhookHandler;
    @Mock
    private GtrAuthService authService;
    @Mock
    private GtrCredentials credentials;
    @InjectMocks
    private GtrProvider provider;

    @Test
    void testGetName() {
        assertEquals(GtrProvider.NAME, provider.getName());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", "  ", "\t", "\n" })
    void testGetWalletInfo_hostingVaspDidNotDefined(String hostingVaspDid) {
        IIdentityWalletEvaluationRequest request = mock(IIdentityWalletEvaluationRequest.class);
        when(request.getDidOfVaspHostingCustodialWallet()).thenReturn(hostingVaspDid);

        ITravelRuleWalletInfo walletInfo = provider.getWalletInfo(request);

        assertEquals(CryptoWalletType.UNKNOWN, walletInfo.getCryptoWalletType());
        assertNull(walletInfo.getOwnerVaspDid());
    }

    @Test
    void testGetWalletInfo_verified() {
        IIdentityWalletEvaluationRequest request = mock(IIdentityWalletEvaluationRequest.class);
        when(request.getDidOfVaspHostingCustodialWallet()).thenReturn("hostingVaspDid");

        GtrVerifyAddressResponse response = new GtrVerifyAddressResponse();
        response.setSuccess(true);

        when(gtrService.getWalletInfo(credentials, request)).thenReturn(response);

        ITravelRuleWalletInfo walletInfo = provider.getWalletInfo(request);

        assertEquals(CryptoWalletType.CUSTODIAL, walletInfo.getCryptoWalletType());
        assertEquals("hostingVaspDid", walletInfo.getOwnerVaspDid());
    }

    @Test
    void testGetWalletInfo_unverified() {
        IIdentityWalletEvaluationRequest request = mock(IIdentityWalletEvaluationRequest.class);
        when(request.getDidOfVaspHostingCustodialWallet()).thenReturn("hostingVaspDid");

        GtrVerifyAddressResponse response = new GtrVerifyAddressResponse();
        response.setSuccess(false);

        when(gtrService.getWalletInfo(credentials, request)).thenReturn(response);

        ITravelRuleWalletInfo walletInfo = provider.getWalletInfo(request);

        assertEquals(CryptoWalletType.UNKNOWN, walletInfo.getCryptoWalletType());
        assertNull(walletInfo.getOwnerVaspDid());
    }

    @Test
    void testGetAllVasps() {
        List<GtrVaspBasicInfo> basicInfoList = List.of(
                createGtrVaspBasicInfo("vasp_code_1", "vasp_name_1"),
                createGtrVaspBasicInfo("vasp_code_2", "vasp_name_2")
        );

        when(gtrService.getAllVasps(credentials)).thenReturn(basicInfoList);

        List<ITravelRuleVasp> vasps = provider.getAllVasps();

        assertEquals(2, vasps.size());
        assertEquals("vasp_code_1", vasps.get(0).getDid());
        assertEquals("vasp_name_1", vasps.get(0).getName());
        assertEquals("vasp_code_2", vasps.get(1).getDid());
        assertEquals("vasp_name_2", vasps.get(1).getName());
    }

    @Test
    void testCreateTransfer() {
        ITravelRuleTransferData transferData = mock(ITravelRuleTransferData.class);
        GtrVerifyPiiResponse verifyPiiResponse = mock(GtrVerifyPiiResponse.class);
        when(verifyPiiResponse.getRequestId()).thenReturn("request_id");

        when(gtrService.createTransfer(credentials, transferData)).thenReturn(verifyPiiResponse);

        ITravelRuleTransferInfo transferInfo = provider.createTransfer(transferData);

        assertEquals("request_id", transferInfo.getId());
    }

    @Test
    void testRegisterStatusUpdateListener() {
        ITravelRuleTransferListener listener = mock(ITravelRuleTransferListener.class);

        boolean result = provider.registerTransferListener(listener);

        assertTrue(result);
        verify(transferHandler, times(1)).registerTransferListener(listener);
    }

    @Test
    void testUnregisterStatusUpdateListener() {
        boolean result = provider.unregisterTransferListener();

        assertTrue(result);
    }

    @Test
    void testUpdateTransfer() {
        ITravelRuleTransferUpdateRequest request = mock(ITravelRuleTransferUpdateRequest.class);
        when(request.getId()).thenReturn("request_id");

        ITravelRuleTransferInfo transferInfo = provider.updateTransfer(request);

        assertEquals("request_id", transferInfo.getId());
        verify(gtrService, times(1)).notifyTxId(credentials, request);
    }

    @Test
    void testNotifyProviderConfigurationChanged() {
        assertDoesNotThrow(() -> provider.notifyProviderConfigurationChanged());
    }

    @Test
    void testTestProviderConfiguration() {
        when(gtrService.testProviderCredentials(credentials)).thenReturn(true);

        boolean result = provider.testProviderConfiguration();

        assertTrue(result);
    }

    @Test
    void testOnTransferResolved_webhookEnabled() {
        ITravelRuleTransferResolvedEvent event = mock(ITravelRuleTransferResolvedEvent.class);

        when(configuration.isWebhooksEnabled()).thenReturn(true);
        when(verifyPiiWebhookHandler.onTransferResolved(event)).thenReturn(true);

        boolean result = provider.onTransferResolved(event);

        assertTrue(result);
    }

    @Test
    void testOnTransferResolved_webhookDisabled() {
        ITravelRuleTransferResolvedEvent event = mock(ITravelRuleTransferResolvedEvent.class);

        when(configuration.isWebhooksEnabled()).thenReturn(false);

        boolean result = provider.onTransferResolved(event);

        assertFalse(result);
        verifyNoInteractions(verifyPiiWebhookHandler);
    }

    @Test
    void testUpdateCredentials_unchanged() {
        mockGtrCredentials();

        GtrCredentials newCredentials = createGtrCredentials(
                "access_key", "signed_secret_key", "curve_public_key", "curve_private_key"
        );

        provider.updateCredentials(newCredentials);

        assertSame(credentials, provider.getCredentials());
        verify(authService, never()).removeAccessToken(any());
    }

    private static Stream<Arguments> testUpdateCredentials_changed_arguments() {
        return Stream.of(
                arguments(createGtrCredentials("ACCESS_KEY", "signed_secret_key", "curve_public_key", "curve_private_key")),
                arguments(createGtrCredentials("access_key", "SIGNED_SECRET_KEY", "curve_public_key", "curve_private_key")),
                arguments(createGtrCredentials("access_key", "signed_secret_key", "CURVE_PUBLIC_KEY", "curve_private_key")),
                arguments(createGtrCredentials("access_key", "signed_secret_key", "curve_public_key", "CURVE_PRIVATE_KEY"))
        );
    }

    @ParameterizedTest
    @MethodSource("testUpdateCredentials_changed_arguments")
    void testUpdateCredentials_changed(GtrCredentials newCredentials) {
        provider.updateCredentials(newCredentials);

        assertNotSame(credentials, provider.getCredentials());
        assertSame(newCredentials, provider.getCredentials());
        verify(authService, times(1)).removeAccessToken(credentials);
    }

    private void mockGtrCredentials() {
        when(credentials.getAccessKey()).thenReturn("access_key");
        when(credentials.getSignedSecretKey()).thenReturn("signed_secret_key");
        when(credentials.getCurvePublicKey()).thenReturn("curve_public_key");
        when(credentials.getCurvePrivateKey()).thenReturn("curve_private_key");
    }

    private GtrVaspBasicInfo createGtrVaspBasicInfo(String code, String name) {
        GtrVaspBasicInfo basicInfo = mock(GtrVaspBasicInfo.class);
        when(basicInfo.getVaspCode()).thenReturn(code);
        when(basicInfo.getVaspName()).thenReturn(name);

        return basicInfo;
    }

    private static GtrCredentials createGtrCredentials(String accessKey,
                                                       String signedSecretKey,
                                                       String curvePublicKey,
                                                       String curvePrivateKey
    ) {
        ITravelRuleProviderCredentials credentials = mock(ITravelRuleProviderCredentials.class);
        when(credentials.getVaspDid()).thenReturn("vasp_code");
        when(credentials.getClientId()).thenReturn(accessKey);
        when(credentials.getPublicKey()).thenReturn(curvePublicKey);
        when(credentials.getPrivateKey()).thenReturn(curvePrivateKey);

        return new GtrCredentials(credentials, clientSecret -> signedSecretKey);
    }

}