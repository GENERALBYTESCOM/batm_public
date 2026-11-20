package com.generalbytes.batm.server.extensions;

import lombok.Data;
import lombok.ToString;

import java.time.OffsetDateTime;

/**
 * Describes how an identity is suspended, see {@link IExtensionContext#suspendIdentity(IdentitySuspension)}.
 * Besides the identity, which is required, a new instance describes a permanent suspension without a reason.
 */
@Data
public class IdentitySuspension {

    /**
     * Public ID of the identity to suspend. Required.
     */
    private String identityPublicId;

    /**
     * When the suspension ends; must be in the future when the identity is suspended.
     * {@code null} (the default) makes the suspension permanent.
     */
    private OffsetDateTime until;

    /**
     * Why the identity is suspended, recorded in the audit log. Can be null (the default).
     */
    @ToString.Exclude
    private String reason;
}
