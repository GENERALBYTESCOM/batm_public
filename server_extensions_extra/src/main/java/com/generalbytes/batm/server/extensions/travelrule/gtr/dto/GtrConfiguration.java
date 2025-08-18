package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.generalbytes.batm.server.extensions.travelrule.gtr.GtrProvider;
import lombok.Data;
import lombok.ToString;

/**
 * Configuration object for {@link GtrProvider} containing data for access to Global Travel Rule (GTR) API.
 */
@Data
@ToString(exclude = {"clientCertificatePassphrase"})
public class GtrConfiguration {
    private String apiUrl;
    /**
     * Prefix used for generating Request ID value.
     * The prefix should ideally specify the operator. Only case-sensitive characters [A-Z], numbers [0-9] and underscore [_] are allowed.
     * The generated Request ID is needed to register the transfer and then call the related GTR endpoints.
     */
    private String requestIdPrefix;
    /**
     * Relative GTR client certificate path in config directory (typically {@code /batm/config}).
     * Used for client authentication as key store in {@code .p12} format.
     */
    private String clientCertificatePath;
    /**
     * Passphrase for access to key store defined in {@link #clientCertificatePath}. Can be {code null} if passphrase is not used.
     */
    private String clientCertificatePassphrase;
    /**
     * Relative GTR server certificate path in config directory (typically {@code /batm/config}).
     * Used for truststore in {@code .pem} format.
     */
    private String gtrServerTrustCertificatePath;
    /**
     * The time in minutes after which the access token expires. The default value is 60 days (86 400 minutes).
     */
    private Integer accessTokenExpirationInMinutes;
    private boolean webhooksEnabled;
}
