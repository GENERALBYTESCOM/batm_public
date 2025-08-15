package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import lombok.Getter;

import java.util.function.UnaryOperator;

/**
 * Data object for GTR credentials.
 */
@Getter
public class GtrCredentials {
    private final String vaspCode;
    private final String accessKey;
    private final String signedSecretKey;
    private final String curvePublicKey;
    private final String curvePrivateKey;

    /**
     * Constructor.
     *
     * @param credentials        {@link ITravelRuleProviderCredentials}
     * @param secretKeyGenerator Secret key generator as {@link UnaryOperator}.
     */
    public GtrCredentials(ITravelRuleProviderCredentials credentials, UnaryOperator<String> secretKeyGenerator) {
        vaspCode = credentials.getVaspDid();
        accessKey =  credentials.getClientId();
        signedSecretKey = secretKeyGenerator.apply(credentials.getClientSecret());
        curvePrivateKey = credentials.getPrivateKey();
        curvePublicKey = credentials.getPublicKey();
    }
}
