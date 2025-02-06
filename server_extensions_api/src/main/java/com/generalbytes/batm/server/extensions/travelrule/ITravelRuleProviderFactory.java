package com.generalbytes.batm.server.extensions.travelrule;

/**
 * Factory for creating travel rule provider instances.
 */
public interface ITravelRuleProviderFactory {

    /**
     * Returns the name of the provider. Used to correctly select what type of provider to create.
     *
     * @return name of the provider as defined by {@link ITravelRuleProvider#getName()}
     */
    String getProviderName();

    /**
     * Creates a new instance of the provider. The provider should be configured with the given credentials.
     * <p>
     * <strong>Warning:</strong> This method is called for every interaction with the provider.
     * Reason is to provide use case specific credentials to the provider. Implementation should be fast and reuse heavy objects.
     * Also don't share credentials between different instances of the provider as they might differ.
     *
     * @param credentials credentials for the provider currently used when processing transaction
     * @return new instance of the provider
     */
    ITravelRuleProvider getProvider(ITravelRuleProviderCredentials credentials);
}