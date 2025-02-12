package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request to generate a new access token.
 *
 * @see <a href="https://devx.notabene.id/docs/auth0">Notabene Documentation</a>
 * @see NotabeneGenerateAccessTokenResponse
 */
@Getter
@Setter
@NoArgsConstructor
public class NotabeneGenerateAccessTokenRequest {

    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
    @JsonProperty("grant_type")
    private String grantType;
    /**
     * {@code https://api.notabene.id} (Production) or {@code https://api.notabene.dev} (Testing)
     */
    @JsonProperty("audience")
    private String audience;

}
