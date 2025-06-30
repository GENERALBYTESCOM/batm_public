package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class InspectionInfoResponse extends JsonObject {
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime inspectionDate;
    private String applicantId;
    private List<InspectionImage> images;
}
