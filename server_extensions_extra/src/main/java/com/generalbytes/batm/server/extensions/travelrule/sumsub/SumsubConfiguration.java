package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * Configuration object for {@link SumsubProvider}.
 */
@Getter
@Setter
@ToString
public class SumsubConfiguration {
    private boolean webhooksEnabled;
    /**
     * Originator (hot) wallet addresses used for outgoing transfers, keyed by crypto asset (e.g. BTC).
     */
    private Map<String, String> originatorWalletAddresses = Map.of();
}
