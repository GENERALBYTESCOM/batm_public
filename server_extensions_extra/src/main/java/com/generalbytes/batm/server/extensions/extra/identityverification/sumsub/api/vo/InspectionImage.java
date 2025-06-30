package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class InspectionImage extends JsonObject {
    private String id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime addedDate;
    private Integer imageId;
    private DocumentDefinition idDocDef;
    private InspectImageReviewResult reviewResult;
}
