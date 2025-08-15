package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Object containing data about the verify field
 * used in response objects {@link GtrVerifyPiiResponse} and {@link GtrWebhookVerifyPiiResponse}.
 */
@Getter
@Setter
public class GtrVerifyField {
    private String message;
    private Integer status;
    private String type;
}
