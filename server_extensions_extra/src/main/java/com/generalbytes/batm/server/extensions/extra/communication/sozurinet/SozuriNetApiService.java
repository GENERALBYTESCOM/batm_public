package com.generalbytes.batm.server.extensions.extra.communication.sozurinet;

import lombok.AllArgsConstructor;

import java.io.IOException;

/**
 * Service for interacting with the SozuriNet API to send SMS messages.
 * Utilizes the {@code ISozuriNetAPI} for API communication.
 */
@AllArgsConstructor
public class SozuriNetApiService {

    private static final String MESSAGE_TYPE = "promotional";

    private final ISozuriNetAPI api;

    /**
     * Sends an SMS message via the SozuriNet API using the provided credentials.
     *
     * @param apiCredentials the credentials required for authenticating to the SozuriNet API
     * @param phoneNumber    the recipient's phone number in international format (e.g., +420123456789)
     * @param message        the SMS text message to be sent
     * @return the response from the SozuriNet API
     * @throws IOException if an I/O error occurs during the communication with the API
     */
    public SozuriNetJsonResponse sendSms(SozuriNetApiCredentials apiCredentials, String phoneNumber, String message) throws IOException {
        SozuriNetSmsRequest request = new SozuriNetSmsRequest(
                apiCredentials.project(),
                apiCredentials.from(),
                phoneNumber,
                apiCredentials.campaign(),
                apiCredentials.channel(),
                apiCredentials.apiKey(),
                message,
                MESSAGE_TYPE
        );
        return api.sendSms(request);
    }
}
