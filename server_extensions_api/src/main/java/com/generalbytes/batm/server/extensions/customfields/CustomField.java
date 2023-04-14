package com.generalbytes.batm.server.extensions.customfields;

import com.generalbytes.batm.server.extensions.customfields.value.CustomFieldValue;

import java.util.Objects;

public class CustomField {
    private final CustomFieldDefinition definition;
    private final CustomFieldValue value;

    public CustomField(CustomFieldDefinition definition, CustomFieldValue value) {
        this.definition = Objects.requireNonNull(definition, "definition cannot be null");
        this.value = Objects.requireNonNull(value, "value cannot be null");
    }

    public CustomFieldDefinition getDefinition() {
        return definition;
    }

    public CustomFieldValue getValue() {
        return value;
    }
}
