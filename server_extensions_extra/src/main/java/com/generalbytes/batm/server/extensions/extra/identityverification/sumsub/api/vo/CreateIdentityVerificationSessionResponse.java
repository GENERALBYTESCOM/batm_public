package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateIdentityVerificationSessionResponse extends JsonObject {
    private String url;

    @Override
    public String toString() {
        return "CreateIdentityVerificationSessionResponse{" +
                "url='" + url + '\'' +
                '}';
    }
}
