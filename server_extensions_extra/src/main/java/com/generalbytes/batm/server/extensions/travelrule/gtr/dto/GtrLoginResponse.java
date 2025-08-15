package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import lombok.Getter;
import lombok.Setter;

/**
 * Response object containing JWT access token.
 * Used in {@link GtrApi#login(GtrLoginRequest)}.
 */
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GtrLoginResponse {
    /**
     * Data object containing JWT access token.
     */
    private LoginData data;

    public String getJwtToken() {
        return data.getJwt();
    }

    @Getter
    private static class LoginData {
        private String jwt;
    }
}
