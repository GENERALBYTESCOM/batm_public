package com.generalbytes.batm.server.extensions.extra.identityverification.veriff;

import com.generalbytes.batm.server.extensions.aml.verification.BiometricAuthenticationRequest;
import com.generalbytes.batm.server.extensions.aml.verification.BiometricAuthenticationResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.CreateIdentityVerificationSessionRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.CreateIdentityVerificationSessionResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.IVeriffApi;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric.SessionDecisionResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric.SubmitSessionRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric.SubmitSessionResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric.UploadMediaRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.biometric.UploadMediaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.InvocationResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VeriffBiometricAuthenticatorTest {

    @Mock
    private IVeriffApi biometricApi;

    private VeriffBiometricAuthenticator authenticator;

    private static final String IDENTITY_PUBLIC_ID = "test-identity-id";
    private static final String SESSION_ID = "test-session-id";

    @BeforeEach
    void setUp() {
        // Use short polling values to make tests run faster
        authenticator = new VeriffBiometricAuthenticator(biometricApi, 100, 500);
    }

    @Test
    void testVerifyBiometricSuccess() throws Exception {
        InputStream biometricData = new ByteArrayInputStream("test-data".getBytes());
        BiometricAuthenticationRequest request = new BiometricAuthenticationRequest(IDENTITY_PUBLIC_ID, biometricData);

        CreateIdentityVerificationSessionResponse sessionResponse = createMockSessionResponse();
        UploadMediaResponse uploadResponse = createMockUploadResponse(true);
        SubmitSessionResponse submitResponse = createMockSubmitResponse(true);
        SessionDecisionResponse decisionResponse = createMockDecisionResponse(true);

        when(biometricApi.createSession(any(CreateIdentityVerificationSessionRequest.class))).thenReturn(sessionResponse);
        when(biometricApi.uploadMedia(eq(SESSION_ID), any(UploadMediaRequest.class))).thenReturn(uploadResponse);
        when(biometricApi.submitSession(eq(SESSION_ID), any(SubmitSessionRequest.class))).thenReturn(submitResponse);
        when(biometricApi.getSessionDecision(SESSION_ID)).thenReturn(decisionResponse);

        BiometricAuthenticationResponse result = authenticator.verifyBiometric(request);

        assertNotNull(result);
        assertTrue(result.success());
        assertEquals("Authentication successful", result.message());
        assertEquals(SESSION_ID, result.sessionId());

        verify(biometricApi).createSession(any(CreateIdentityVerificationSessionRequest.class));
        verify(biometricApi).uploadMedia(eq(SESSION_ID), any(UploadMediaRequest.class));
        verify(biometricApi).submitSession(eq(SESSION_ID), any(SubmitSessionRequest.class));
        verify(biometricApi).getSessionDecision(SESSION_ID);
    }

    @Test
    void testVerifyBiometricCreateSessionFailed() throws Exception {
        InputStream biometricData = new ByteArrayInputStream("test-data".getBytes());
        BiometricAuthenticationRequest request = new BiometricAuthenticationRequest(IDENTITY_PUBLIC_ID, biometricData);

        when(biometricApi.createSession(any(CreateIdentityVerificationSessionRequest.class)))
            .thenThrow(new HttpStatusIOException("Error", new InvocationResult("Error body", 400)));

        BiometricAuthenticationResponse result = authenticator.verifyBiometric(request);

        assertNotNull(result);
        assertFalse(result.success());
        assertTrue(result.message().contains("Failed to create authentication session"));
        assertNull(result.sessionId());

        verify(biometricApi).createSession(any(CreateIdentityVerificationSessionRequest.class));
        verify(biometricApi, never()).uploadMedia(anyString(), any(UploadMediaRequest.class));
        verify(biometricApi, never()).submitSession(anyString(), any(SubmitSessionRequest.class));
        verify(biometricApi, never()).getSessionDecision(anyString());
    }

    @Test
    void testVerifyBiometricNullSessionResponse() throws Exception {
        InputStream biometricData = new ByteArrayInputStream("test-data".getBytes());
        BiometricAuthenticationRequest request = new BiometricAuthenticationRequest(IDENTITY_PUBLIC_ID, biometricData);

        when(biometricApi.createSession(any(CreateIdentityVerificationSessionRequest.class))).thenReturn(null);

        BiometricAuthenticationResponse result = authenticator.verifyBiometric(request);

        assertNotNull(result);
        assertFalse(result.success());
        assertTrue(result.message().contains("Failed to create authentication session"));
        assertNull(result.sessionId());
    }

    @Test
    void testVerifyBiometricUploadFailed() throws Exception {
        InputStream biometricData = new ByteArrayInputStream("test-data".getBytes());
        BiometricAuthenticationRequest request = new BiometricAuthenticationRequest(IDENTITY_PUBLIC_ID, biometricData);

        CreateIdentityVerificationSessionResponse sessionResponse = createMockSessionResponse();
        UploadMediaResponse uploadResponse = createMockUploadResponse(false);

        when(biometricApi.createSession(any(CreateIdentityVerificationSessionRequest.class))).thenReturn(sessionResponse);
        when(biometricApi.uploadMedia(eq(SESSION_ID), any(UploadMediaRequest.class))).thenReturn(uploadResponse);

        BiometricAuthenticationResponse result = authenticator.verifyBiometric(request);

        assertNotNull(result);
        assertFalse(result.success());
        assertTrue(result.message().contains("Failed to upload biometric data"));
        assertNull(result.sessionId());

        verify(biometricApi).createSession(any(CreateIdentityVerificationSessionRequest.class));
        verify(biometricApi).uploadMedia(eq(SESSION_ID), any(UploadMediaRequest.class));
        verify(biometricApi, never()).submitSession(anyString(), any(SubmitSessionRequest.class));
        verify(biometricApi, never()).getSessionDecision(anyString());
    }

    @Test
    void testVerifyBiometricUploadException() throws Exception {
        InputStream biometricData = new ByteArrayInputStream("test-data".getBytes());
        BiometricAuthenticationRequest request = new BiometricAuthenticationRequest(IDENTITY_PUBLIC_ID, biometricData);

        CreateIdentityVerificationSessionResponse sessionResponse = createMockSessionResponse();

        when(biometricApi.createSession(any(CreateIdentityVerificationSessionRequest.class))).thenReturn(sessionResponse);
        when(biometricApi.uploadMedia(eq(SESSION_ID), any(UploadMediaRequest.class)))
            .thenThrow(new IOException("Upload error"));

        BiometricAuthenticationResponse result = authenticator.verifyBiometric(request);

        assertNotNull(result);
        assertFalse(result.success());
        assertTrue(result.message().contains("Failed to upload biometric data"));
        assertNull(result.sessionId());
    }

    @Test
    void testVerifyBiometricSubmitFailed() throws Exception {
        InputStream biometricData = new ByteArrayInputStream("test-data".getBytes());
        BiometricAuthenticationRequest request = new BiometricAuthenticationRequest(IDENTITY_PUBLIC_ID, biometricData);

        CreateIdentityVerificationSessionResponse sessionResponse = createMockSessionResponse();
        UploadMediaResponse uploadResponse = createMockUploadResponse(true);
        SubmitSessionResponse submitResponse = createMockSubmitResponse(false);

        when(biometricApi.createSession(any(CreateIdentityVerificationSessionRequest.class))).thenReturn(sessionResponse);
        when(biometricApi.uploadMedia(eq(SESSION_ID), any(UploadMediaRequest.class))).thenReturn(uploadResponse);
        when(biometricApi.submitSession(eq(SESSION_ID), any(SubmitSessionRequest.class))).thenReturn(submitResponse);

        BiometricAuthenticationResponse result = authenticator.verifyBiometric(request);

        assertNotNull(result);
        assertFalse(result.success());
        assertTrue(result.message().contains("Failed to submit authentication session"));
        assertNull(result.sessionId());

        verify(biometricApi).createSession(any(CreateIdentityVerificationSessionRequest.class));
        verify(biometricApi).uploadMedia(eq(SESSION_ID), any(UploadMediaRequest.class));
        verify(biometricApi).submitSession(eq(SESSION_ID), any(SubmitSessionRequest.class));
        verify(biometricApi, never()).getSessionDecision(anyString());
    }

    @Test
    void testVerifyBiometricSubmitException() throws Exception {
        InputStream biometricData = new ByteArrayInputStream("test-data".getBytes());
        BiometricAuthenticationRequest request = new BiometricAuthenticationRequest(IDENTITY_PUBLIC_ID, biometricData);

        CreateIdentityVerificationSessionResponse sessionResponse = createMockSessionResponse();
        UploadMediaResponse uploadResponse = createMockUploadResponse(true);

        when(biometricApi.createSession(any(CreateIdentityVerificationSessionRequest.class))).thenReturn(sessionResponse);
        when(biometricApi.uploadMedia(eq(SESSION_ID), any(UploadMediaRequest.class))).thenReturn(uploadResponse);
        when(biometricApi.submitSession(eq(SESSION_ID), any(SubmitSessionRequest.class)))
            .thenThrow(new IOException("Submit error"));

        BiometricAuthenticationResponse result = authenticator.verifyBiometric(request);

        assertNotNull(result);
        assertFalse(result.success());
        assertTrue(result.message().contains("Failed to submit authentication session"));
        assertNull(result.sessionId());
    }

    @Test
    void testVerifyBiometricDecisionDeclined() throws Exception {
        InputStream biometricData = new ByteArrayInputStream("test-data".getBytes());
        BiometricAuthenticationRequest request = new BiometricAuthenticationRequest(IDENTITY_PUBLIC_ID, biometricData);

        CreateIdentityVerificationSessionResponse sessionResponse = createMockSessionResponse();
        UploadMediaResponse uploadResponse = createMockUploadResponse(true);
        SubmitSessionResponse submitResponse = createMockSubmitResponse(true);
        SessionDecisionResponse decisionResponse = createMockDecisionResponse(false);

        when(biometricApi.createSession(any(CreateIdentityVerificationSessionRequest.class))).thenReturn(sessionResponse);
        when(biometricApi.uploadMedia(eq(SESSION_ID), any(UploadMediaRequest.class))).thenReturn(uploadResponse);
        when(biometricApi.submitSession(eq(SESSION_ID), any(SubmitSessionRequest.class))).thenReturn(submitResponse);
        when(biometricApi.getSessionDecision(SESSION_ID)).thenReturn(decisionResponse);

        BiometricAuthenticationResponse result = authenticator.verifyBiometric(request);

        assertNotNull(result);
        assertFalse(result.success());
        assertTrue(result.message().contains("Authentication declined"));
        assertNull(result.sessionId());

        verify(biometricApi).createSession(any(CreateIdentityVerificationSessionRequest.class));
        verify(biometricApi).uploadMedia(eq(SESSION_ID), any(UploadMediaRequest.class));
        verify(biometricApi).submitSession(eq(SESSION_ID), any(SubmitSessionRequest.class));
        verify(biometricApi).getSessionDecision(SESSION_ID);
    }

    @Test
    void testVerifyBiometricDecisionInProgress() throws Exception {
        InputStream biometricData = new ByteArrayInputStream("test-data".getBytes());
        BiometricAuthenticationRequest request = new BiometricAuthenticationRequest(IDENTITY_PUBLIC_ID, biometricData);

        CreateIdentityVerificationSessionResponse sessionResponse = createMockSessionResponse();
        UploadMediaResponse uploadResponse = createMockUploadResponse(true);
        SubmitSessionResponse submitResponse = createMockSubmitResponse(true);
        SessionDecisionResponse decisionResponse = createMockDecisionInProgressResponse();

        when(biometricApi.createSession(any(CreateIdentityVerificationSessionRequest.class))).thenReturn(sessionResponse);
        when(biometricApi.uploadMedia(eq(SESSION_ID), any(UploadMediaRequest.class))).thenReturn(uploadResponse);
        when(biometricApi.submitSession(eq(SESSION_ID), any(SubmitSessionRequest.class))).thenReturn(submitResponse);
        when(biometricApi.getSessionDecision(SESSION_ID)).thenReturn(decisionResponse);

        BiometricAuthenticationResponse result = authenticator.verifyBiometric(request);

        assertNotNull(result);
        assertFalse(result.success());
        assertTrue(result.message().contains("Authentication is still in progress"));
        assertNull(result.sessionId());
    }

    private CreateIdentityVerificationSessionResponse createMockSessionResponse() {
        CreateIdentityVerificationSessionResponse response = new CreateIdentityVerificationSessionResponse();
        response.verification = new CreateIdentityVerificationSessionResponse.Verification();
        response.verification.id = SESSION_ID;
        response.verification.url = "https://veriff.com/test-url";
        return response;
    }

    private UploadMediaResponse createMockUploadResponse(boolean success) {
        return new UploadMediaResponse(success ? "success" : "error");
    }

    private SubmitSessionResponse createMockSubmitResponse(boolean success) {
        return new SubmitSessionResponse(success ? "success" : "error");
    }

    private SessionDecisionResponse createMockDecisionResponse(boolean approved) {
        return new SessionDecisionResponse(
            "success",
            new SessionDecisionResponse.Verification(
                SESSION_ID,
                approved ? "approved" : "declined",
                approved ? null : "Face does not match",
                IDENTITY_PUBLIC_ID
            )
        );
    }

    private SessionDecisionResponse createMockDecisionInProgressResponse() {
        return new SessionDecisionResponse(
            "success",
            new SessionDecisionResponse.Verification(
                SESSION_ID,
                "submitted",
                null,
                IDENTITY_PUBLIC_ID
            )
        );
    }
}