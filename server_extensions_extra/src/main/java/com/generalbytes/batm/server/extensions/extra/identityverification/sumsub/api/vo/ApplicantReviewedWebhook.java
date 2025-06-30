package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import lombok.Getter;

@Getter
public class ApplicantReviewedWebhook extends BaseWebhookBody {
    private ApplicantReviewResult reviewResult;
}