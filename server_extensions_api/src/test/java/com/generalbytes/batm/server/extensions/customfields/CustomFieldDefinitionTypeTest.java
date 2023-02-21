package com.generalbytes.batm.server.extensions.customfields;

import com.generalbytes.batm.server.extensions.customfields.value.StringCustomFieldValue;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CustomFieldDefinitionTypeTest {

    @Test
    public void isValueTypeAllowed() {
        StringCustomFieldValue str = new StringCustomFieldValue("test");
        assertTrue(CustomFieldDefinitionType.PARAGRAPH.isValueTypeAllowed(str.getClass()));
        assertTrue(CustomFieldDefinitionType.SINGLE_LINE.isValueTypeAllowed(str.getClass()));
        assertFalse(CustomFieldDefinitionType.DROPDOWN.isValueTypeAllowed(str.getClass()));
        assertFalse(CustomFieldDefinitionType.RADIO_BTN.isValueTypeAllowed(str.getClass()));
        assertFalse(CustomFieldDefinitionType.CHECKBOX.isValueTypeAllowed(str.getClass()));
        assertFalse(CustomFieldDefinitionType.IMAGE.isValueTypeAllowed(str.getClass()));
        assertFalse(CustomFieldDefinitionType.DOCUMENT.isValueTypeAllowed(str.getClass()));
    }
}