package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration object for {@link SumsubProvider}.
 */
@Getter
@Setter
@ToString
public class SumsubConfiguration {
    private boolean webhooksEnabled;
}
