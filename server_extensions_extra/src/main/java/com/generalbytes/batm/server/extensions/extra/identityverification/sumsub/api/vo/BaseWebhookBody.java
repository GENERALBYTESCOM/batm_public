package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
public class BaseWebhookBody extends JsonObject {
    private String applicantId;
    private String inspectionId;
    private String correlationId;
    private String externalUserId;
    private String type;
    private String reviewStatus;
    private String levelName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ssZ")
    private OffsetDateTime createdAt;
}

