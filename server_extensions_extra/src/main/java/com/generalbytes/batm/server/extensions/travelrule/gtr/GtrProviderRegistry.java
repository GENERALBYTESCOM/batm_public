package com.generalbytes.batm.server.extensions.travelrule.gtr;

import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Register of created GTR provider instances for various VASP DIDs based on Travel Rule Settings.
 *
 * <p>It is needed when {@link GtrVerifyPiiService#processVerifyPiiWebhookMessage}
 * to find the correct provider and obtain the correct curve private key to decrypt the message.
 */
@AllArgsConstructor
public class GtrProviderRegistry {

    /**
     * Already initialized GTR providers. VASP DID is used as a key to avoid multiple initializations based on same settings.
     */
    private final Map<String, GtrProvider> initializedGtrProviders = new ConcurrentHashMap<>();

    /**
     * Returns an already initialized GTR provider according to the specified VASP DID.
     * If the provider has not yet been initialized with the specified VASP DID, it returns {@code null}.
     *
     * @param vaspDid VASP DID.
     * @return A {@link GtrProvider} if already initialized, otherwise {@code null}.
     */
    public GtrProvider get(String vaspDid) {
        return initializedGtrProviders.get(vaspDid);
    }

    /**
     * Puts the GTR provider to the registry.
     *
     * @param vaspDid     VASP DID.
     * @param gtrProvider A {@link GtrProvider} to be inserted to the registry.
     */
    public void put(String vaspDid, GtrProvider gtrProvider) {
        initializedGtrProviders.put(vaspDid, gtrProvider);
    }

}
