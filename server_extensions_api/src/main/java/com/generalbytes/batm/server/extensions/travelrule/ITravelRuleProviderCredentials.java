package com.generalbytes.batm.server.extensions.travelrule;

/**
 * This interface serves as a container for the minimal set of information needed
 * to authenticate and identify a travel rule provider.
 */
public interface ITravelRuleProviderCredentials {
    /**
     * The client ID configured for the travel rule provider, used to authenticate with the travel rule provider
     */
    String getClientId();

    /**
     * Client secret configured for the travel rule provider, used to authenticate with the travel rule provider
     */
    String getClientSecret();

    /**
     * The configured VASP used based on the travel rule settings for this provider. Represents operator's VASP DID.
     * Value depends on the context and may differ when different travel rule settings are used.
     */
    String getVaspDid();

    /**
     * Configured public key that can be used, for example, by a counterparty when encrypting/decrypting payload.
     */
    String getPublicKey();

    /**
     * Configured private key that may be needed, for example, when encrypting/decrypting payload.
     */
    String getPrivateKey();
}
