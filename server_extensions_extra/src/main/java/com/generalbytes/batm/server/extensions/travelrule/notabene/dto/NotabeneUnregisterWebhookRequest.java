package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request to unregister the multi-message Webhook URL for a given VASP.
 *
 * @see <a href="https://devx.notabene.id/reference/deletewebhook">Notabene Documentation</a>
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class NotabeneUnregisterWebhookRequest {

    @JsonProperty("vaspDID")
    private String vaspDid;

}
