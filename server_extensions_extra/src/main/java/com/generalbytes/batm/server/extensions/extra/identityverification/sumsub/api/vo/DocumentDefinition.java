package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.SumSubDocumentType;
import lombok.Getter;

@Getter
public class DocumentDefinition extends JsonObject {
    // alpha 3 code
    private String country;
    private SumSubDocumentType idDocType;
}
