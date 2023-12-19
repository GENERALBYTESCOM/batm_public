package com.generalbytes.batm.server.extensions.customfields.value;

import com.generalbytes.batm.server.extensions.customfields.CustomFieldDefinitionType;

import java.time.LocalDate;

/**
 * used with {@link CustomFieldDefinitionType#DATE}
 */
public class LocalDateCustomFieldValue implements CustomFieldValue {
    private final LocalDate localDateValue;

    public LocalDateCustomFieldValue(LocalDate localDateValue) {
        this.localDateValue = localDateValue;
    }

    public LocalDate getLocalDateValue() {
        return localDateValue;
    }
}
