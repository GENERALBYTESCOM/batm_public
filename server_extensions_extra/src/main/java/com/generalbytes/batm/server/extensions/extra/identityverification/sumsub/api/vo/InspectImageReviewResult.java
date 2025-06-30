package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.ReviewAnswer;
import lombok.Getter;

@Getter
public class InspectImageReviewResult extends JsonObject {
    private ReviewAnswer reviewAnswer;
}
