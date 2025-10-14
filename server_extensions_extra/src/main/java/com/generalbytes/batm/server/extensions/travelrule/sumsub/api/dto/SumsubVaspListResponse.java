package com.generalbytes.batm.server.extensions.travelrule.sumsub.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/**
 * Response object containing all available VASPs on Sumsub.
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SumsubVaspListResponse {

    private final List<VaspDetail> items;

    @JsonCreator
    public SumsubVaspListResponse(@JsonProperty("list") VaspList list) {
        this.items = list.items;
    }

    /**
     * Object containing list of VASPs.
     */
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class VaspList {
        private List<VaspDetail> items;
    }

    /**
     * Object containing info about VASP.
     */
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VaspDetail {
        private String id;
        private String name;
    }
}
