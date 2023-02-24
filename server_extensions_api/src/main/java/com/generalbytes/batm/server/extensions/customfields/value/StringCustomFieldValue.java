package com.generalbytes.batm.server.extensions.customfields.value;

import com.generalbytes.batm.server.extensions.customfields.CustomFieldDefinitionType;

/**
 * used with {@link CustomFieldDefinitionType#SINGLE_LINE} and {@link CustomFieldDefinitionType#PARAGRAPH}
 */
public class StringCustomFieldValue implements CustomFieldValue {
    private final String stringValue;

    public StringCustomFieldValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
