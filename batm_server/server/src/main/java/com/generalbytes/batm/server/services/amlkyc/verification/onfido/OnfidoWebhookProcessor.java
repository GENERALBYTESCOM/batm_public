package com.generalbytes.batm.server.services.amlkyc.verification.onfido;

import com.generalbytes.batm.server.ServerConfigForAll;
import com.generalbytes.batm.server.common.data.Organization;
import com.generalbytes.batm.server.common.data.amlkyc.ApplicantCheckResult;
import com.generalbytes.batm.server.common.data.amlkyc.IdentityApplicant;
import com.generalbytes.batm.server.dao.JPADao;
import com.generalbytes.batm.server.dao.JPAUtil;
import com.generalbytes.batm.server.services.amlkyc.verification.VerificationResultProcessor;
import com.generalbytes.batm.server.services.web.IdentityCheckWebhookException;
import com.onfido.Onfido;
import com.onfido.exceptions.OnfidoException;
import com.onfido.models.Check;
import com.onfido.models.Webhook;
import com.onfido.webhooks.WebhookEvent;
import com.onfido.webhooks.WebhookEventVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Onfido has a list of webhooks configured, and it sends the events to ALL of them.
 * We keep a map of Webhook Key -> webhook configuration entry.
 * For onfido implementation Webhook key is database organization ID.
 * When starting a verification:
 * - we check if there is a webhook for this organization (webhook key) in our local map
 * - if not we check if the webhook for this organization (webhook key) is configured on onfido
 * - - if yes, we download it and keep it in the local map
 * - - if not we create it on onfido and store it in the local map
 * This could be done with multiple organizations on the same or different server with the same Onfido account (API keys)
 * When a verification is finished, Onfido notifies ALL the webhooks configured for that Onfido account.
 * That will result in one call to {@link #process(String, String, String)} per webhook configured on onfido i.e. per organization using the same API keys.
 *
 * When webhook is received:
 * - we get the webhookKey from the URL
 * - find an organization with organization ID = webhook Key
 * - find Onfido API key from the Organization Identity Verification Provider
 * - get webhook configuration by the webhook key from the local map
 * - if it's not there (e.g. after server restart) we fetch it from onfido and put it in the map
 * - from the webhook configuration we get a webhook "token"
 * - the token is used to verify webhook's signature
 * - only after the signature is verified we can accept the information sent in the webhook
 */
public class OnfidoWebhookProcessor {
    private static final Logger log = LoggerFactory.getLogger(OnfidoWebhookProcessor.class);
    static final Map<String, Webhook> HOOKS = new HashMap<>();

    private final JPADao jpaDao;
    private final Onfido onfido;
    private final OnfidoVerificationResultMapper checkResultMapper;
    private final VerificationResultProcessor verificationResultProcessor = new VerificationResultProcessor();

    public OnfidoWebhookProcessor(Onfido onfido, JPADao jpaDao, String webhookKey) {
        this.jpaDao = jpaDao;
        this.onfido = onfido;
        this.checkResultMapper = new OnfidoVerificationResultMapper(onfido);

        getOrCreateWebhook(webhookKey);
    }

    private Webhook getOrCreateWebhook(String webhookKey) {
        Webhook webhook = HOOKS.get(webhookKey);
        if (webhook != null) {
            log.info("Using webhook from local cache for URL={}", webhook.getUrl());
            return webhook;
        }
        Webhook downloadedWebhook = fetchOrCreateWebhook(getWebhookMasterUrl(webhookKey));
        HOOKS.put(webhookKey, downloadedWebhook);
        return downloadedWebhook;
    }

    private String getWebhookMasterUrl(String webhookKey) {
        return ServerConfigForAll.getMasterServerProxyAddress() + "/serverapi/apiv1/identity-check/onfidowh/" + webhookKey;
    }

    private Webhook fetchOrCreateWebhook(String webhookMasterUrl) {
        return OnfidoIdentityVerificationProvider.callInTry(() -> {
            Webhook hook = onfido.webhook.list().stream().filter(webhook -> webhook.getUrl().equals(webhookMasterUrl)).findFirst().orElse(null);
            if (hook != null) {
                log.info("Using existing webhook configured on onfido for URL={}", webhookMasterUrl);
                return hook;
            }
            log.info("Creating new webhook on onfido with URL={}", webhookMasterUrl);
            return onfido.webhook.create(Webhook.request().url(webhookMasterUrl));
        });
    }

    public void process(String rawPayload, String signature, String webhookKey) throws IdentityCheckWebhookException {
        try {
            WebhookEvent event = getWebhookEvent(rawPayload, signature, webhookKey);
            log.debug("Received onfido event {}: id {} status {}", event.getAction(), event.getObject().getId(), event.getObject().getStatus());

            if (event.getAction().equals("check.completed")) {
                Check check = onfido.check.find(event.getObject().getId());
                IdentityApplicant identityApplicant = jpaDao.getApplicantByApplicantId(check.getApplicantId());
                if (identityApplicant == null) {
                    log.error("Identity applicant {} not found. Skipping check result processing.", check.getApplicantId());
                    return;
                }
                Organization applicantOrganization = identityApplicant.getOrganization();
                if (!Long.toString(applicantOrganization.getId()).equals(webhookKey)) {
                    log.info("Ignoring webhook for different organization; webhookKey: {}, identityApplicant: {}, organization ID: {}"
                            + " - are you using the same onfido API keys with multiple organizations?",
                        webhookKey, identityApplicant, applicantOrganization.getId());
                    return;
                }
                ApplicantCheckResult result = checkResultMapper.mapResult(check, identityApplicant);
                verificationResultProcessor.process(result);
                jpaDao.update(result);
            }
        } catch (OnfidoException e) {
            throw new IdentityCheckWebhookException(Response.Status.INTERNAL_SERVER_ERROR, "failed to process webhook", rawPayload, e);
        } finally {
            JPAUtil.releaseEntityManagerWithCommit();
        }
    }

    private WebhookEvent getWebhookEvent(String rawPayload, String signature, String webhookKey) throws IdentityCheckWebhookException {
        try {
            Webhook webhook = getOrCreateWebhook(webhookKey);
            return new WebhookEventVerifier(webhook.getToken()).readPayload(rawPayload, signature);
        } catch (OnfidoException e) {
            // signature mismatch could happen if two organizations are using the same Onfido API keys on the same server.
            // The request would come multiple times, but only one with a valid signature.
            // Delete any unused/old webhooks on https://dashboard.onfido.com/api/webhook_management
            throw new IdentityCheckWebhookException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to parse event", rawPayload, e);
        }
    }
}
