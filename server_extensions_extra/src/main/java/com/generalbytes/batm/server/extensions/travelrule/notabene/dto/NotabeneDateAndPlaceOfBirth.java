package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Holds the date and place of birth.
 */
@Getter
@Setter
@NoArgsConstructor
public class NotabeneDateAndPlaceOfBirth {

    /**
     * A point in time, represented as a day within the calendar year.
     * Compliant with ISO 8601. (YYYY-MM-DD)
     */
    private String dateOfBirth;
    private String placeOfBirth;

}
