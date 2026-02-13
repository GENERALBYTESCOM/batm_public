package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents the request for network test received from GTR as a webhook message.
 *
 * @see <a href="https://www.globaltravelrule.com/documentation/standard2-api/callback-api-network-health-api">GTR documentation</a>
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName(GtrApiConstants.CallbackType.NETWORK_TEST + "")
public class GtrNetworkTestWebhookPayload implements GtrWebhookPayload {

    private String callbackType;

}
