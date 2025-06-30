package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ApplicantInfoResponse extends JsonObject {
    @Setter
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String key;
    private String inspectionId;
    private String externalUserId;
    private ApplicantInfo info;
    private List<Questionnaire> questionnaires;
}
