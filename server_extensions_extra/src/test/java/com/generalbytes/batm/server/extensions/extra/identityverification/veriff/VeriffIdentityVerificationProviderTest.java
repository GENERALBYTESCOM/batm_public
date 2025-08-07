package com.generalbytes.batm.server.extensions.extra.identityverification.veriff;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.aml.verification.BiometricAuthenticationRequest;
import com.generalbytes.batm.server.extensions.aml.verification.BiometricAuthenticationResponse;
import com.generalbytes.batm.server.extensions.aml.verification.CreateApplicantResponse;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.CreateIdentityVerificationSessionRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.CreateIdentityVerificationSessionResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.IVeriffApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.InvocationResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VeriffIdentityVerificationProviderTest {

    @Mock
    private IVeriffApi api;

    @Mock
    private VeriffWebhookProcessor webhookProcessor;

    @Mock
    private VeriffBiometricAuthenticator biometricAuthenticator;

    @Mock
    private IExtensionContext extensionContext;

    private VeriffIdentityVerificationProvider provider;

    private static final String PUBLIC_KEY = "test-public-key";
    private static final String PRIVATE_KEY = "test-private-key";

    @BeforeEach
    void setUp() {
        provider = new VeriffIdentityVerificationProvider(api, webhookProcessor, biometricAuthenticator);
    }

    @Test
    void testConstructorWithBiometricConfig() {
        VeriffIdentityVerificationProvider providerInstance = new VeriffIdentityVerificationProvider(PUBLIC_KEY, PRIVATE_KEY);
        assertNotNull(providerInstance);
    }

    @Test
    void testConstructorWithDependencies() {
        VeriffIdentityVerificationProvider providerInstance = new VeriffIdentityVerificationProvider(
            api, webhookProcessor, biometricAuthenticator);

        assertNotNull(providerInstance);
    }

    @Test
    void testCreateApplicantSuccess() throws Exception {
        String identityPublicId = "test-identity-id";
        String customerLanguage = "en";
        String applicantId = "test-applicant-id";
        String verificationUrl = "https://veriff.com/test-url";

        CreateIdentityVerificationSessionResponse response = new CreateIdentityVerificationSessionResponse();
        response.verification = new CreateIdentityVerificationSessionResponse.Verification();
        response.verification.id = applicantId;
        response.verification.url = verificationUrl;

        when(api.createSession(any(CreateIdentityVerificationSessionRequest.class))).thenReturn(response);

        CreateApplicantResponse result = provider.createApplicant(customerLanguage, identityPublicId);

        assertNotNull(result);
        assertEquals(applicantId, result.getApplicantId());
        assertEquals(verificationUrl, result.getVerificationWebUrl());

        ArgumentCaptor<CreateIdentityVerificationSessionRequest> requestCaptor =
            ArgumentCaptor.forClass(CreateIdentityVerificationSessionRequest.class);
        verify(api).createSession(requestCaptor.capture());
    }

    @Test
    void testCreateApplicantHttpError() throws Exception {
        String identityPublicId = "test-identity-id";
        String customerLanguage = "en";

        when(api.createSession(any(CreateIdentityVerificationSessionRequest.class)))
            .thenThrow(new HttpStatusIOException("Error", new InvocationResult("Error body", 400)));

        CreateApplicantResponse result = provider.createApplicant(customerLanguage, identityPublicId);

        assertNull(result);
    }

    @Test
    void testCreateApplicantGenericError() throws Exception {
        String identityPublicId = "test-identity-id";
        String customerLanguage = "en";

        when(api.createSession(any(CreateIdentityVerificationSessionRequest.class)))
            .thenThrow(new RuntimeException("Error"));

        CreateApplicantResponse result = provider.createApplicant(customerLanguage, identityPublicId);

        assertNull(result);
    }

    @Test
    void testProcessWebhookEvent() throws Exception {
        String rawPayload = "{\"test\":\"data\"}";
        String signature = "test-signature";

        provider.processWebhookEvent(rawPayload, signature);

        verify(webhookProcessor).process(rawPayload, signature);
    }

    @Test
    void testProcessWebhookEventError() throws Exception {
        String rawPayload = "{\"test\":\"data\"}";
        String signature = "test-signature";

        doThrow(new IdentityCheckWebhookException(400, "Error", "Error message"))
            .when(webhookProcessor).process(anyString(), anyString());

        assertThrows(IdentityCheckWebhookException.class,
            () -> provider.processWebhookEvent(rawPayload, signature));
    }

    @Test
    void testVerifyBiometric() {
        String identityPublicId = "test-identity-id";
        InputStream biometricData = new ByteArrayInputStream("test-data".getBytes());
        BiometricAuthenticationRequest request = new BiometricAuthenticationRequest(identityPublicId, biometricData);
        BiometricAuthenticationResponse expectedResponse = BiometricAuthenticationResponse.success("test-session-id");

        when(biometricAuthenticator.verifyBiometric(any(BiometricAuthenticationRequest.class)))
            .thenReturn(expectedResponse);

        BiometricAuthenticationResponse result = provider.verifyBiometric(request);

        assertNotNull(result);
        assertEquals(expectedResponse.success(), result.success());
        assertEquals(expectedResponse.message(), result.message());
        assertEquals(expectedResponse.sessionId(), result.sessionId());

        verify(biometricAuthenticator).verifyBiometric(request);
    }

    @Test
    void testVerifyBiometricNotConfigured() {
        VeriffIdentityVerificationProvider withoutBiometric = new VeriffIdentityVerificationProvider(PUBLIC_KEY, PRIVATE_KEY);
        String identityPublicId = "test-identity-id";
        ByteArrayInputStream biometricData = new ByteArrayInputStream("test-data".getBytes());
        BiometricAuthenticationRequest request = new BiometricAuthenticationRequest(identityPublicId, biometricData);

        BiometricAuthenticationResponse result = withoutBiometric.verifyBiometric(request);

        assertNotNull(result);
        assertFalse(result.success());
        assertEquals("Biometric authentication is not configured", result.message());
        assertNull(result.sessionId());
    }

    @Test
    void testGetForGlobalServer() {
        try (MockedStatic<VeriffExtension> mockedExtension = mockStatic(VeriffExtension.class)) {
            mockedExtension.when(VeriffExtension::getExtensionContext).thenReturn(extensionContext);
            when(extensionContext.isGlobalServer()).thenReturn(true);
            when(extensionContext.getConfigProperty(eq("veriff"), eq("public_key"), any())).thenReturn(PUBLIC_KEY);
            when(extensionContext.getConfigProperty(eq("veriff"), eq("private_key"), any())).thenReturn(PRIVATE_KEY);

            VeriffIdentityVerificationProvider result = VeriffIdentityVerificationProvider.getForGlobalServer();

            assertNotNull(result);
        }
    }

    @Test
    void testGetForGlobalServerNotGlobal() {
        try (MockedStatic<VeriffExtension> mockedExtension = mockStatic(VeriffExtension.class)) {
            mockedExtension.when(VeriffExtension::getExtensionContext).thenReturn(extensionContext);
            when(extensionContext.isGlobalServer()).thenReturn(false);

            assertThrows(RuntimeException.class, VeriffIdentityVerificationProvider::getForGlobalServer);
        }
    }

    @Test
    void testGetForGlobalServerMissingKey() {
        try (MockedStatic<VeriffExtension> mockedExtension = mockStatic(VeriffExtension.class)) {
            mockedExtension.when(VeriffExtension::getExtensionContext).thenReturn(extensionContext);
            when(extensionContext.isGlobalServer()).thenReturn(true);
            when(extensionContext.getConfigProperty(eq("veriff"), eq("public_key"), any())).thenReturn(null);

            assertThrows(NullPointerException.class, VeriffIdentityVerificationProvider::getForGlobalServer);
        }
    }
}