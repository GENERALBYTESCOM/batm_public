package com.generalbytes.batm.server.extensions.customfields.value;

import com.generalbytes.batm.server.extensions.customfields.CustomFieldDefinitionType;

/**
 * used with {@link CustomFieldDefinitionType#IMAGE} and {@link CustomFieldDefinitionType#DOCUMENT}
 */
public class FileCustomFieldValue implements CustomFieldValue {
    private final String fileName;
    private final String mimeType;

    public FileCustomFieldValue(String fileName, String mimeType) {
        this.fileName = fileName;
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }
}
