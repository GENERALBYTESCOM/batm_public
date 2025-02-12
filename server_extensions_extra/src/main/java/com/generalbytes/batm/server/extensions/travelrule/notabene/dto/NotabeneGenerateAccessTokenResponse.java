package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response to generating a new access token.
 *
 * @see <a href="https://devx.notabene.id/docs/auth0">Notabene Documentation</a>
 * @see NotabeneGenerateAccessTokenRequest
 */
@Getter
@Setter
@NoArgsConstructor
public class NotabeneGenerateAccessTokenResponse {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private int expiresInMillis;
    @JsonProperty("token_type")
    private String tokenType;

}
