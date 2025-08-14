package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;

import lombok.AllArgsConstructor;

import java.io.IOException;

/**
 * Service for interacting with the SMSBrána.cz API to send SMS messages. Utilizes the {@code ISMSBranaCZAPI} for API communication.
 */
@AllArgsConstructor
public class SMSBranaCZApiService {

    private static final String SEND_SMS_ACTION = "send_sms";
    private static final String DATA_CODE_ALLOWED_CHARS_WITH_DIACRITICS = "ucs2";
    private final ISMSBranaCZAPI api;

    public String sendSms(SMSBranaCZApiCredentials apiCredentials, String phoneNumber, String message) throws IOException {
        return api.sendSms(
                apiCredentials.login(),
                apiCredentials.time(),
                apiCredentials.salt(),
                apiCredentials.auth(),
                SEND_SMS_ACTION,
                phoneNumber,
                message,
                DATA_CODE_ALLOWED_CHARS_WITH_DIACRITICS
        );
    }
}
