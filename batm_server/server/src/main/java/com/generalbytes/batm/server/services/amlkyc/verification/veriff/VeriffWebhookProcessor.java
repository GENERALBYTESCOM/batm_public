package com.generalbytes.batm.server.services.amlkyc.verification.veriff;

import com.generalbytes.batm.server.common.data.Organization;
import com.generalbytes.batm.server.common.data.amlkyc.ApplicantCheckResult;
import com.generalbytes.batm.server.common.data.amlkyc.IdentityApplicant;
import com.generalbytes.batm.server.dao.JPADao;
import com.generalbytes.batm.server.dao.JPAUtil;
import com.generalbytes.batm.server.services.amlkyc.verification.VerificationResultProcessor;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.webhook.VerificationDecisionWebhookRequest;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.VeriffDigest;
import com.generalbytes.batm.server.services.amlkyc.verification.veriff.api.webhook.VerificationEventWebhookRequest;
import com.generalbytes.batm.server.services.web.IdentityCheckWebhookException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;

public class VeriffWebhookProcessor {
    private static final Logger log = LoggerFactory.getLogger("batm.master.VeriffWebhookProcessor");
    private static final VeriffWebhookParser veriffWebhookParser = new VeriffWebhookParser();

    private final VeriffDigest veriffDigest;
    private final VeriffVerificationResultMapper checkResultMapper = new VeriffVerificationResultMapper();
    private final VerificationResultProcessor verificationResultProcessor = new VerificationResultProcessor();

    public VeriffWebhookProcessor(VeriffDigest veriffDigest) {
        this.veriffDigest = veriffDigest;
    }

    /**
     * @return organization for the applicant that started the verification session.
     * Request signature must be verified after getting the organization.
     */
    public static Organization getOrganization(String rawPayload, JPADao jpaDao) throws IdentityCheckWebhookException {
        String applicantId = veriffWebhookParser.getApplicantId(rawPayload);
        IdentityApplicant identityApplicant = jpaDao.getApplicantByApplicantId(applicantId);
        if (identityApplicant == null) {
            throw new IdentityCheckWebhookException(Response.Status.INTERNAL_SERVER_ERROR, "identity applicant not found by verification session ID", rawPayload);
        }
        Organization organization = identityApplicant.getOrganization();
        log.info("Organization found: {} for applicant ID: {}", organization, applicantId);
        return organization;
    }

    public void process(String rawPayload, String signature) throws IdentityCheckWebhookException {
        verifySignature(rawPayload, signature);
        veriffWebhookParser.accept(rawPayload, this::processDecisionWebhook, this::processEventWebhook);
    }

    private void processEventWebhook(String rawPayload, VerificationEventWebhookRequest request) {
        log.info("Veriff event webhook received: {}", rawPayload);
    }

    private void processDecisionWebhook(String rawPayload, VerificationDecisionWebhookRequest request) throws IdentityCheckWebhookException {
        log.info("Veriff decision webhook received: {}", rawPayload);
        try {
            JPADao jpaDao = JPADao.getInstance();
            String applicantId = request.getApplicantId();
            IdentityApplicant identityApplicant = jpaDao.getApplicantByApplicantId(applicantId);
            if (identityApplicant == null) {
                throw new IdentityCheckWebhookException(Response.Status.INTERNAL_SERVER_ERROR, "applicant not found", applicantId);
            }
            ApplicantCheckResult result = checkResultMapper.mapResult(request, identityApplicant);
            verificationResultProcessor.process(result);
            jpaDao.update(result);
        } finally {
            JPAUtil.releaseEntityManagerWithCommit();
        }
    }

    private void verifySignature(String rawPayload, String signature) throws IdentityCheckWebhookException {
        String computedSignature = veriffDigest.digest(rawPayload);
        if (!computedSignature.equals(signature)) {
            throw new IdentityCheckWebhookException(Response.Status.UNAUTHORIZED, "signature verification failure", "Wrong api secret used? computed: '" + computedSignature + "', received: '" + signature + "', payload: '" + rawPayload + "'. Are Veriff API keys configured properly? Is this an old webhook with different API keys that got re-sent?");
        }
    }

}
