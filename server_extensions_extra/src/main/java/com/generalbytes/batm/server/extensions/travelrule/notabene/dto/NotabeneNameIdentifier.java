package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Full name separated into primary and secondary identifier.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class NotabeneNameIdentifier {

    private String primaryIdentifier;
    private String secondaryIdentifier;
    /**
     * A single value corresponding to the nature of name being adopted.
     */
    private NotabeneNameIdentifierType nameIdentifierType;

}
