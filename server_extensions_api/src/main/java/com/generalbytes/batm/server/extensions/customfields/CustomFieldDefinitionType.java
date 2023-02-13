package com.generalbytes.batm.server.extensions.customfields;

/**
 * Defines data (value) type of custom field
 */
public enum CustomFieldDefinitionType {
    /**
     * single line string value
     */
    SINGLE_LINE,
    /**
     * multi line string value
     */
    PARAGRAPH,
    /**
     * A set of options (see {@link CustomFieldDefinition#getElements()}, only one option could be chosen / selected.
     * Presented as multiple radio buttons.
     */
    RADIO_BTN,
    /**
     * A set of options (seel {@link CustomFieldDefinition#getElements()}, only one option could be chosen / selected.
     * Presented as a drop-down menu.
     */
    DROPDOWN,
    /**
     * A single boolean (yes/no) value, displayed as a checkbox
     */
    CHECKBOX,
    /**
     * Image or photo file
     */
    IMAGE,
    /**
     * Document scan or other file
     */
    DOCUMENT;
}
