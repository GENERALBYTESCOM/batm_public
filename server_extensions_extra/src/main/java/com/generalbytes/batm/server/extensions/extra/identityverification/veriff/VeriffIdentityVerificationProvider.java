package com.generalbytes.batm.server.extensions.extra.identityverification.veriff;

import com.generalbytes.batm.server.extensions.IIdentity;
import com.generalbytes.batm.server.extensions.aml.verification.CreateApplicantResponse;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.CreateIdentityVerificationSessionRequest;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.CreateIdentityVerificationSessionResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.IVeriffApi;
import com.generalbytes.batm.server.extensions.extra.identityverification.veriff.api.VeriffDigest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

import java.util.Objects;

public class VeriffIdentityVerificationProvider implements IIdentityVerificationProvider {

    private static final Logger log = LoggerFactory.getLogger("batm.master.VeriffIdentityVerificationProvider");

    private final IVeriffApi api;
    private final VeriffWebhookProcessor veriffWebhookProcessor;

    public VeriffIdentityVerificationProvider(String publicKey, String privateKey) {
        Objects.requireNonNull(publicKey, "veriff public key cannot be null");
        Objects.requireNonNull(privateKey, "veriff private key cannot be null");
        VeriffDigest veriffDigest = new VeriffDigest(privateKey);
        api = IVeriffApi.create(publicKey, veriffDigest);
        veriffWebhookProcessor = new VeriffWebhookProcessor(publicKey, veriffDigest, api);
    }

    /**
     * @param identity null when called from standalone server to global server
     * @param vendorData sent to veriff, sent back by veriff to us in the webhook and displayed in veriff dashboard
     */
    @Override
    public CreateApplicantResponse createApplicant(IIdentity identity, String gbApiKey, String customerLanguage, String vendorData) {
        try {
            CreateIdentityVerificationSessionResponse createSessionResponse = api.createSession(CreateIdentityVerificationSessionRequest.create(vendorData));
            log.info("Received {} for {}", createSessionResponse, identity);
            String verificationWebUrl = createSessionResponse.verification.url;

            return new CreateApplicantResponse(createSessionResponse.getApplicantId(), null, verificationWebUrl);

        } catch (HttpStatusIOException e) {
            log.error("Error creating Veriff session, HTTP response code: {}, body: {}", e.getHttpStatusCode(), e.getHttpBody());
        } catch (Exception e) {
            log.error("Error creating Veriff session", e);
        }
        return null;
    }

    /**
     * Processes verification results sent from the verification provider
     */
    public void processWebhookEvent(String rawPayload, String signature) throws IdentityCheckWebhookException {
        veriffWebhookProcessor.process(rawPayload, signature);
    }
}

