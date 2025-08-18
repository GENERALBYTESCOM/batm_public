package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import lombok.Getter;
import lombok.Setter;

/**
 * Request object containing data for generate JWT access token.
 * Used in {@link GtrApi#login(GtrLoginRequest)}.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class GtrLoginRequest {
    /**
     * Code of VASP.
     */
    private String vaspCode;
    /**
     * Access key.
     */
    private String accessKey;
    /**
     * Signed secret key (SHA-512 hash of secret key).
     */
    private String signedSecretKey;
    /**
     * JWT token validity period (in minutes). Optional, if not set, the token has unlimited validity.
     */
    private Integer expireInMinutes;
}
