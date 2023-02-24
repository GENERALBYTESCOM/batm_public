package com.generalbytes.batm.server.extensions.customfields;

import java.util.List;

public class CustomFieldDefinition {
    private final long id;
    private final String name;
    private final String label;
    private final String description;
    private final String validator;
    private final CustomFieldDefinitionType type;
    private final CustomFieldDefinitionAvailability availability;
    private final List<Element> elements;

    public CustomFieldDefinition(long id, String name, String label, String description, String validator, CustomFieldDefinitionType type, CustomFieldDefinitionAvailability availability, List<Element> elements) {
        this.id = id;
        this.name = name;
        this.label = label;
        this.description = description;
        this.validator = validator;
        this.type = type;
        this.availability = availability;
        this.elements = elements;
    }

    public static class Element {
        private final long id;
        private final String value;

        public Element(long id, String value) {
            this.id = id;
            this.value = value;
        }

        public long getId() {
            return id;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * Used when setting a custom field value to define which custom field the value belongs to
     */
    public long getId() {
        return id;
    }

    /**
     * Displayed in CAS
     */
    public String getName() {
        return name;
    }

    /**
     * Displayed when a user fills in a value (e.g. on terminal screen)
     */
    public String getLabel() {
        return label;
    }

    /**
     * Displayed when a user fills in a value (e.g. on terminal screen)
     */
    public String getDescription() {
        return description;
    }

    /**
     * regex to validate the value; could be null to allow any value
     */
    public String getValidator() {
        return validator;
    }

    public CustomFieldDefinitionType getType() {
        return type;
    }

    public CustomFieldDefinitionAvailability getAvailability() {
        return availability;
    }

    /**
     * For custom field types that has some options to chose from (drop-down, radio button) this lists the possible options
     */
    public List<Element> getElements() {
        return elements;
    }
}
