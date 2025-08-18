package com.generalbytes.batm.server.extensions.travelrule.gtr.dto;

import lombok.Getter;

/**
 * Object containing detail data about VASP.
 * Used in {@link GtrVaspResponse}.
 */
@Getter
public class GtrVaspInfo extends GtrVaspBasicInfo {
    private String contactInfo;
}
