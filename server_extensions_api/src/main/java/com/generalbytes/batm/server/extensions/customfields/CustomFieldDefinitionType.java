package com.generalbytes.batm.server.extensions.customfields;

import com.generalbytes.batm.server.extensions.customfields.value.BooleanCustomFieldValue;
import com.generalbytes.batm.server.extensions.customfields.value.ChoiceCustomFieldValue;
import com.generalbytes.batm.server.extensions.customfields.value.CustomFieldValue;
import com.generalbytes.batm.server.extensions.customfields.value.FileCustomFieldValue;
import com.generalbytes.batm.server.extensions.customfields.value.StringCustomFieldValue;

/**
 * Defines data (value) type of custom field
 */
public enum CustomFieldDefinitionType {
    /**
     * single line string value
     */
    SINGLE_LINE(StringCustomFieldValue.class),
    /**
     * multi line string value
     */
    PARAGRAPH(StringCustomFieldValue.class),
    /**
     * A set of options (see {@link CustomFieldDefinition#getElements()}, only one option could be chosen / selected.
     * Presented as multiple radio buttons.
     */
    RADIO_BTN(ChoiceCustomFieldValue.class),
    /**
     * A set of options (seel {@link CustomFieldDefinition#getElements()}, only one option could be chosen / selected.
     * Presented as a drop-down menu.
     */
    DROPDOWN(ChoiceCustomFieldValue.class),
    /**
     * A single boolean (yes/no) value, displayed as a checkbox
     */
    CHECKBOX(BooleanCustomFieldValue.class),
    /**
     * Image or photo file
     */
    IMAGE(FileCustomFieldValue.class),
    /**
     * Document scan or other file
     */
    DOCUMENT(FileCustomFieldValue.class);

    private final Class<? extends CustomFieldValue> allowedValueType;

    CustomFieldDefinitionType(Class<? extends CustomFieldValue> allowedValueType) {
        this.allowedValueType = allowedValueType;
    }

    /**
     * @return true if a custom field with this type could be used with the specified value type
     */
    public <T extends CustomFieldValue> boolean isValueTypeAllowed(Class<T> value) {
        return value.isAssignableFrom(allowedValueType);
    }
}
