package com.generalbytes.batm.server.extensions.travelrule.gtr.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * General Global Travel Rule (GTR) API error response.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GtrApiError {
    /**
     * Response message.
     *
     * @see <a href="https://www.globaltravelrule.com/documentation/api-references/enum/ResponseMessageEnum">Global Travel Rule (GTR) documentation</a>
     */
    private String msg;
    /**
     * Status code.
     *
     * @see <a href="https://www.globaltravelrule.com/documentation/api-references/enum/VerifyStatusEnum">Global Travel Rule (GTR) documentation</a>
     */
    private Integer verifyStatus;
    /**
     * Detail message.
     *
     * @see <a href="https://www.globaltravelrule.com/documentation/api-references/enum/VerifyStatusEnum">Global Travel Rule (GTR) documentation</a>
     */
    private String verifyMessage;
}
