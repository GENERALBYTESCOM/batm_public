package com.generalbytes.batm.server.extensions.travelrule.sumsub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Register of created Sumsub provider instances for various VASP DIDs based on Travel Rule Settings.
 */
public class SumsubProviderRegistry {

    /**
     * Already initialized Sumsub providers. VASP DID is used as a key to avoid multiple initializations based on same settings.
     */
    private final Map<String, SumsubProvider> initializedSumsubProviders = new ConcurrentHashMap<>();

    /**
     * Returns an already initialized Sumsub provider according to the specified VASP DID.
     * If the provider has not yet been initialized with the specified VASP DID, it returns {@code null}.
     *
     * @param vaspDid VASP DID.
     * @return A {@link SumsubProvider} if already initialized, otherwise {@code null}.
     */
    public SumsubProvider get(String vaspDid) {
        return initializedSumsubProviders.get(vaspDid);
    }

    /**
     * Puts the Sumsub provider to the registry.
     *
     * @param vaspDid     VASP DID.
     * @param sumsubProvider A {@link SumsubProvider} to be inserted to the registry.
     */
    public void put(String vaspDid, SumsubProvider sumsubProvider) {
        initializedSumsubProviders.put(vaspDid, sumsubProvider);
    }

}
