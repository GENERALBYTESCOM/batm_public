package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the response from the SumSub API for an applicant's data.
 * <a href="https://docs.sumsub.com/reference/get-applicant-data">API reference</a>
 */
@Getter
public class ApplicantInfoResponse extends JsonObject {
    @Setter
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private String key;
    private String inspectionId;
    private String externalUserId;
    // data extracted from applicant's documents by SumSub OCR/recognition
    private ApplicantInfo info;
    // data submitted by the applicant (e.g., manually entered address)
    private ApplicantInfo fixedInfo;
    private String email;
    private List<Questionnaire> questionnaires;
}
