package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import lombok.Getter;

/**
 * Holds Query parameters for the List VASPs endpoint.
 *
 * @see <a href="https://devx.notabene.id/reference/tfsimplelistvasps-1">Notabene Documentation</a>
 */
@Getter
public class NotabeneListVaspsQueryParams {

    /**
     * String to query.
     */
    private final String query;
    /**
     * True to return all found records at once. False to page the response.
     */
    private final Boolean all;

    public NotabeneListVaspsQueryParams() {
        this.query = null;
        this.all = true;
    }

    public NotabeneListVaspsQueryParams(String query) {
        this.query = query;
        this.all = true;
    }

}
