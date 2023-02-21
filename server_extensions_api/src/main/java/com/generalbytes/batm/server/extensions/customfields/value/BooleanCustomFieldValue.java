package com.generalbytes.batm.server.extensions.customfields.value;

import com.generalbytes.batm.server.extensions.customfields.CustomFieldDefinitionType;

/**
 * used with {@link CustomFieldDefinitionType#CHECKBOX}
 */

public class BooleanCustomFieldValue implements CustomFieldValue{
    private final boolean booleanValue;

    public BooleanCustomFieldValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }
}
