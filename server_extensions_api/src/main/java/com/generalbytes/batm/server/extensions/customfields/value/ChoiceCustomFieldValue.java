package com.generalbytes.batm.server.extensions.customfields.value;

import com.generalbytes.batm.server.extensions.customfields.CustomFieldDefinition;
import com.generalbytes.batm.server.extensions.customfields.CustomFieldDefinitionType;

/**
 * used with {@link CustomFieldDefinitionType#DROPDOWN} and {@link CustomFieldDefinitionType#RADIO_BTN}
 */
public class ChoiceCustomFieldValue implements CustomFieldValue{
    private final long choiceId;

    /**
     * @param choiceId id of a choice from {@link CustomFieldDefinition.Element#getId()}
     */

    public ChoiceCustomFieldValue(long choiceId) {
        this.choiceId = choiceId;
    }

    public long getChoiceId() {
        return choiceId;
    }
}
