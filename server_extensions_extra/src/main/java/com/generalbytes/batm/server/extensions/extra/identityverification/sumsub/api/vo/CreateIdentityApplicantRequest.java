package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import lombok.Getter;

@Getter
public class CreateIdentityApplicantRequest {
    private final String externalUserId;
    private final String type;

    public CreateIdentityApplicantRequest(String externalUserId) {
        this.externalUserId = externalUserId;
        this.type = "individual";
    }

}
