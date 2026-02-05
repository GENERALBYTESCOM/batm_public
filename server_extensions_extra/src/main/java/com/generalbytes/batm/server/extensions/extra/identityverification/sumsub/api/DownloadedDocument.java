package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api;

import java.util.Arrays;
import java.util.Objects;

record DownloadedDocument(byte[] content, String contentType) {
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DownloadedDocument that)) return false;
        return Objects.deepEquals(content(), that.content()) && Objects.equals(contentType(), that.contentType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(content()), contentType());
    }

    @Override
    public String toString() {
        return "DownloadedDocument{" +
            "contentType='" + contentType + '\'' +
            '}';
    }
}
