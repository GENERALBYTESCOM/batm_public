package com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101;

import lombok.Getter;
import lombok.Setter;

/**
 * An object representing the IVMS101 payload for PII verification via GTR.
 *
 * @see <a href="https://www.globaltravelrule.com/documentation/pii-verify-fields">GTR documentation: PII Verify Fields</a>
 * @see <a href="https://www.globaltravelrule.com/documentation/ivms-101-guidelines">GTR documentation: Format Guidelines</a>
 */
@Getter
@Setter
public class GtrIvms101Payload {
    private GtrIvms101 ivms101;
}
