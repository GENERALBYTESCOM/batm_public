package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Holds information about Notabene Api pagination.
 *
 * @see NotabeneListVaspsResponse
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotabeneApiPagination {

    /**
     * Current page number. Starts at zero.
     */
    private int page;
    /**
     * Records per page.
     */
    @JsonProperty("per_page")
    private int pageSize;
    /**
     * Field to order by.
     *
     * <p>Order direction is specified by leading 'ASC' or 'DESC'. Examples:</p>
     * <ul>
     *     <li>{@code "name:ASC"} -> Order by name in ascending direction.</li>
     *     <li>{@code "name:DESC"} -> Order by name in descending direction.</li>
     * </ul>
     */
    @JsonProperty("order")
    private String orderBy;
    /**
     * Total records.
     */
    private int total;

}
