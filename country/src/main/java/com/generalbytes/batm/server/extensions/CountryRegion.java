package com.generalbytes.batm.server.extensions;

/**
 * A marker interface for a country region (state/province/territory/...).
 */
public interface CountryRegion {
    /**
     * Retrieves the ISO code representing a specific country region.
     *
     * @return the ISO code as a string
     */
    String getIso();

    /**
     * Retrieves the name of the province/state/territory/...
     *
     * @return the name of the province as a string
     */
    String getProvinceName();
}
