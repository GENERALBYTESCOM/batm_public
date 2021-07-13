package com.generalbytes.batm.server.extensions;

/**
 * Brief representation of an organization.
 */
public interface IOrganization {
    /**
     * Organization ID.
     *
     * @return string value of ID
     */
    String getId();

    /**
     * Organization name
     *
     * @return organization name
     */
    String getName();
}