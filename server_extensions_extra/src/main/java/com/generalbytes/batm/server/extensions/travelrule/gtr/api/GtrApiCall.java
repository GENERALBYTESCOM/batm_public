package com.generalbytes.batm.server.extensions.travelrule.gtr.api;

/**
 * Represents a call to Global Travel Rule (GTR) API.
 *
 * @param <T> Type of the expected response.
 */
@FunctionalInterface
public interface GtrApiCall<T> {

    /**
     * Make a call to Global Travel Rule (GTR) API.
     *
     * @param authorization Authorization header content.
     * @return The response from the call.
     */
    T execute(String authorization);

}
