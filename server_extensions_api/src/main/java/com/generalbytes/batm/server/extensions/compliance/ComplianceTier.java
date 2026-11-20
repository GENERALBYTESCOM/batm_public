package com.generalbytes.batm.server.extensions.compliance;

import com.generalbytes.batm.server.extensions.IExtensionContext;

/**
 * A compliance tier that limits can be calculated for.
 *
 * <p>Returned by {@link IExtensionContext#getComplianceTiers()}.</p>
 *
 * @param id   unique ID of the tier
 * @param name name of the tier
 */
public record ComplianceTier(long id, String name) {

    /**
     * Validates that the tier has a name.
     */
    public ComplianceTier {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
    }
}
