package com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * An object representing the Person Name object in {@link GtrNaturalPerson}.
 */
@Getter
@Setter
public class GtrPersonName {
    @JsonProperty("nameIdentifier")
    private List<GtrPersonNameIdentifier> nameIdentifiers;
}
