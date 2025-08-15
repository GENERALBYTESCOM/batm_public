package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApi;
import lombok.Getter;
import lombok.Setter;

/**
 * Response object containing basic data about VASP.
 * Used in {@link GtrApi#vaspDetail(String, String)}.
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GtrVaspResponse {
    private GtrVaspInfo data;
}
