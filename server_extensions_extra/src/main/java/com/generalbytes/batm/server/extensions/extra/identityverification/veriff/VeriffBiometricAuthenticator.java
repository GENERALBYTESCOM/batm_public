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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class responsible for handling biometric authentication via Veriff API.
 * It uses the Veriff API to create a session, upload biometric data and submit the session. It polls for the result.
 */
@Slf4j
@RequiredArgsConstructor
public class VeriffBiometricAuthenticator {

    private static final int DEFAULT_POLLING_INTERVAL_MS = 3000; // 3 seconds
    private static final int DEFAULT_POLLING_TIMEOUT_MS = 180_000; // 180 seconds

    private final IVeriffApi biometricApi;
    private final int pollingIntervalMs;
    private final int pollingTimeoutMs;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Creates a new biometric authenticator with default polling configuration
     *
     * @param biometricApi Veriff API instance configured with biometric authentication keys
     */
    public VeriffBiometricAuthenticator(IVeriffApi biometricApi) {
        this(biometricApi, DEFAULT_POLLING_INTERVAL_MS, DEFAULT_POLLING_TIMEOUT_MS);
    }

    /**
     * Performs biometric authentication for an existing identity, the result is obtained via polling the Veriff API.
     */
    public BiometricAuthenticationResponse verifyBiometric(BiometricAuthenticationRequest biometricAuthenticationRequest) {
        try {
            String identityPublicId = biometricAuthenticationRequest.identityPublicId();
            log.debug("Starting biometric authentication for identity: {}", identityPublicId);

            // Step 1: Create a session
            CreateIdentityVerificationSessionResponse sessionResponse = createBiometricSession(identityPublicId);
            if (sessionResponse == null || sessionResponse.verification == null || sessionResponse.verification.id == null) {
                return BiometricAuthenticationResponse.failure("Failed to create authentication session");
            }

            String sessionId = sessionResponse.verification.id;
            log.debug("Created biometric authentication session: {} for identity: {}", sessionId, identityPublicId);

            // Step 2: Upload the biometric data
            boolean uploadSuccess = uploadBiometricData(sessionId, biometricAuthenticationRequest.biometricData());
            if (!uploadSuccess) {
                return BiometricAuthenticationResponse.failure("Failed to upload biometric data");
            }

            // Step 3: Submit the session
            boolean submitSuccess = submitSession(sessionId);
            if (!submitSuccess) {
                return BiometricAuthenticationResponse.failure("Failed to submit authentication session");
            }

            // Step 4: Poll for the result
            SessionDecisionResponse decision = pollForDecision(sessionId);
            if (decision == null) {
                return BiometricAuthenticationResponse.failure("Failed to get authentication decision (timeout - got no response)");
            }

            if (decision.isApproved()) {
                return BiometricAuthenticationResponse.success(sessionId);
            } else if (decision.isInProgress()) {
                return BiometricAuthenticationResponse.failure("Authentication is still in progress: timeout after " + pollingTimeoutMs + " ms");
            } else {
                String reason = decision.verification() != null ? decision.verification().reason() : "Unknown reason";
                return BiometricAuthenticationResponse.failure("Authentication declined: " + reason);
            }

        } catch (Exception e) {
            log.error("Error during biometric authentication", e);
            return BiometricAuthenticationResponse.failure("Error during authentication: " + e.getMessage());
        }
    }

    /**
     * Creates a session for biometric authentication
     *
     * @param identityPublicId The public ID of the identity to authenticate
     * @return Session response or null if failed
     */
    private CreateIdentityVerificationSessionResponse createBiometricSession(String identityPublicId) {
        try {
            CreateIdentityVerificationSessionRequest request = CreateIdentityVerificationSessionRequest.create(identityPublicId);
            return biometricApi.createSession(request);
        } catch (Exception e) {
            log.error("Error creating biometric authentication session", e);
            return null;
        }
    }

    /**
     * Uploads biometric data to a session
     *
     * @param sessionId     ID of the session
     * @param biometricData InputStream containing the biometric data
     * @return true if successful, false otherwise
     */
    private boolean uploadBiometricData(String sessionId, InputStream biometricData) {
        try {
            String base64Data = convertInputStreamToBase64(biometricData);
            if (base64Data == null) {
                log.error("Failed to convert biometric data to Base64");
                return false;
            }

            UploadMediaRequest request = UploadMediaRequest.createBiometricRequest(base64Data);
            UploadMediaResponse response = biometricApi.uploadMedia(sessionId, request);
            return response != null && response.isSuccess();
        } catch (Exception e) {
            log.error("Error uploading biometric data", e);
            return false;
        }
    }

    private static String convertInputStreamToBase64(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            byte[] bytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            log.error("Error converting InputStream to Base64", e);
            return null;
        }
    }

    /**
     * Submits a session for authentication
     *
     * @param sessionId ID of the session
     * @return true if successful, false otherwise
     */
    private boolean submitSession(String sessionId) {
        try {
            SubmitSessionRequest request = SubmitSessionRequest.createSubmitRequest();
            SubmitSessionResponse response = biometricApi.submitSession(sessionId, request);
            return response != null && response.isSuccess();
        } catch (Exception e) {
            log.error("Error submitting session", e);
            return false;
        }
    }

    /**
     * Polls for the decision of an authentication session
     *
     * @param sessionId ID of the session
     * @return Decision response or null if timeout
     */
    private SessionDecisionResponse pollForDecision(String sessionId) {
        CompletableFuture<SessionDecisionResponse> future = new CompletableFuture<>();
        AtomicReference<SessionDecisionResponse> lastDecision = new AtomicReference<>();

        ScheduledFuture<?> pollTask = scheduler.scheduleAtFixedRate(() -> {
            try {
                SessionDecisionResponse decision = biometricApi.getSessionDecision(sessionId);
                log.debug("Polling biometric authentication decision for session {}: {}", sessionId, decision);
                lastDecision.set(decision);
                if (decision != null && !decision.isInProgress()) {
                    future.complete(decision);
                }
            } catch (Exception e) {
                log.error("Error when polling for biometric authentication decision", e);
            }
        }, 0, pollingIntervalMs, TimeUnit.MILLISECONDS);

        try {
            return future.orTimeout(pollingTimeoutMs, TimeUnit.MILLISECONDS)
                .whenComplete((result, error) -> pollTask.cancel(false))
                .join();
        } catch (CompletionException e) {
            if (e.getCause() instanceof TimeoutException) {
                log.warn("Timeout while polling for biometric authentication decision for session: {}", sessionId);
                return lastDecision.get();
            }
            throw e;
        }
    }
}