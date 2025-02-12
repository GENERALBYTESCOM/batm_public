package com.generalbytes.batm.server.extensions.travelrule.notabene.api;

/**
 * Represents a call to Notabene API.
 *
 * @param <T> Type of the expected response.
 */
@FunctionalInterface
public interface NotabeneApiCall<T> {

    /**
     * Make a call to Notabene API.
     *
     * @param authorization Authorization header content.
     * @return The response from the call.
     */
    T execute(String authorization);

}
