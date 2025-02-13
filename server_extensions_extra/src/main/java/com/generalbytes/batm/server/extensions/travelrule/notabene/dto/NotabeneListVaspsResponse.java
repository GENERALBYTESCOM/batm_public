package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Response of listing VASPs.
 *
 * @see <a href="https://devx.notabene.id/reference/tfsimplelistvasps-1">Notabene Documentation</a>
 * @see NotabeneVaspInfoSimple
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotabeneListVaspsResponse {

    /**
     * The retrieved VASPs.
     *
     * @see NotabeneVaspInfoSimple
     */
    private List<NotabeneVaspInfoSimple> vasps;
    /**
     * Information about endpoint pagination.
     *
     * @see NotabeneApiPagination
     */
    private NotabeneApiPagination pagination;

}
