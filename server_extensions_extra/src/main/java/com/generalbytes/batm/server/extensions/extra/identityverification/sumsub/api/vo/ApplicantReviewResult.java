package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.ReviewAnswer;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.ReviewRejectType;
import lombok.Getter;

import java.util.List;

@Getter
public class ApplicantReviewResult extends JsonObject {
    private String moderationComment;
    private String clientComment;
    private ReviewAnswer reviewAnswer;
    private List<String> rejectLabels;
    private ReviewRejectType reviewRejectType;
}
