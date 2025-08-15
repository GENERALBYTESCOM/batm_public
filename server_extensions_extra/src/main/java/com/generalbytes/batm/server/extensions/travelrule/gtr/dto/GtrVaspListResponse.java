package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Response object containing basic data about all VASPs.
 * Used in {@link GtrApi#listVasps(String)}.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GtrVaspListResponse {
    private List<GtrVaspBasicInfo> data;
}
