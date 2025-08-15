package com.generalbytes.batm.server.extensions.travelrule.gtr.dto.ivms101;

import lombok.Getter;
import lombok.Setter;

/**
 * An object representing the Person Name Identifier object in {@link GtrPersonName}.
 */
@Getter
@Setter
public class GtrPersonNameIdentifier {
    private String nameIdentifierType;
    private String primaryIdentifier;
    private String secondaryIdentifier;
}
