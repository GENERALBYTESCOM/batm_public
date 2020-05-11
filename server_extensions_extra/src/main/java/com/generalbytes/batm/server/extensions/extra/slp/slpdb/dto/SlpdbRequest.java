/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/package com.generalbytes.batm.server.extensions.extra.slp.slpdb.dto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

public abstract class SlpdbRequest {
    protected final String encoded;

    @Override
    public String toString() {
        return encoded;
    }

    public SlpdbRequest(String templateFileName, Object... args) throws IOException {
        try (InputStream is = Objects.requireNonNull(getClass().getResourceAsStream(templateFileName), templateFileName + " not found")) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            String template = baos.toString(StandardCharsets.UTF_8.name());
            String queryJson = String.format(template, args);
            encoded = Base64.getEncoder().encodeToString(queryJson.getBytes(StandardCharsets.UTF_8));
        }
    }
}
