package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantIdResponse extends JsonObject {
    private String id;

    @Override
    public String toString() {
        return "ApplicantIDResponse{" +
                "id='" + id + '\'' +
                '}';
    }
}
