package com.generalbytes.batm.server.extensions.customfields;

/**
 * Contains the custom field's value. Data type of the value is defined by {@link CustomFieldDefinitionType)} from {@link CustomFieldDefinition}
 */
public class CustomFieldValue {
    private final String stringValue;
    private final Long choiceId;
    private final Boolean booleanValue;
    private final String fileName;
    private final String mimeType;


    private CustomFieldValue(String stringValue, Long choiceId, Boolean booleanValue, String fileName, String mimeType) {
        this.stringValue = stringValue;
        this.choiceId = choiceId;
        this.booleanValue = booleanValue;
        this.fileName = fileName;
        this.mimeType = mimeType;
    }

    /**
     * used with {@link CustomFieldDefinitionType#SINGLE_LINE} and {@link CustomFieldDefinitionType#PARAGRAPH}
     */
    public static CustomFieldValue string(String value) {
        return new CustomFieldValue(value, null, null, null, null);
    }

    /**
     * used with {@link CustomFieldDefinitionType#CHECKBOX}
     */
    public static CustomFieldValue bool(boolean value) {
        return new CustomFieldValue(null, null, value, null, null);
    }

    /**
     * used with {@link CustomFieldDefinitionType#DROPDOWN} and {@link CustomFieldDefinitionType#RADIO_BTN}
     * @param choiceId id of a choice from {@link CustomFieldDefinition.Element#getId()}
     */
    public static CustomFieldValue choice(long choiceId) {
        return new CustomFieldValue(null, choiceId, null, null, null);
    }

    /**
     * used with {@link CustomFieldDefinitionType#IMAGE} and {@link CustomFieldDefinitionType#DOCUMENT}
     */
    public static CustomFieldValue file(String fileName, String mimeType) {
        return new CustomFieldValue(null, null, null, fileName, mimeType);
    }


    /**
     * used with {@link CustomFieldDefinitionType#SINGLE_LINE} and {@link CustomFieldDefinitionType#PARAGRAPH}
     */
    public String getStringValue() {
        return stringValue;
    }

    /**
     * used with {@link CustomFieldDefinitionType#DROPDOWN} and {@link CustomFieldDefinitionType#RADIO_BTN}
     */
    public Long getChoiceId() {
        return choiceId;
    }

    /**
     * used with {@link CustomFieldDefinitionType#CHECKBOX}
     */
    public Boolean getBooleanValue() {
        return booleanValue;
    }

    /**
     * used with {@link CustomFieldDefinitionType#IMAGE} and {@link CustomFieldDefinitionType#DOCUMENT}
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * used with {@link CustomFieldDefinitionType#IMAGE} and {@link CustomFieldDefinitionType#DOCUMENT}
     */
    public String getMimeType() {
        return mimeType;
    }
}
