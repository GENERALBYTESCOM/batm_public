package com.generalbytes.batm.server.extensions.extra.communication.sozurinet;

import com.generalbytes.batm.server.extensions.communication.ICommunicationProvider;
import com.generalbytes.batm.server.extensions.communication.ISmsResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import si.mazi.rescu.HttpStatusIOException;

import java.io.IOException;

/**
 * Provider class for sending SMS using SozuriNet communication service.
 * This class implements the {@link ICommunicationProvider} interface and integrates with the
 * SozuriNet API. It handles authentication, sending SMS, and processing API responses.
 * <p>
 * Setup:
 * 1. Register at <a href="https://sozuri.net/">here</a>
 * 2. Obtain API credentials (apiKey, project, from, campaign, and channel)
 * 3. Use apiKey:project:from:campaign:channel format in BATM SMS provider configuration
 * API Documentation: <a href="https://sozuri.net/docs/text">here</a>
 */
@Slf4j
@AllArgsConstructor
public class SozuriNetProvider implements ICommunicationProvider {
    private final SozuriNetApiService apiService;
    private final SozuriNetCredentialsService credentialsService;

    @Override
    public String getName() {
        return "SozuriNet";
    }

    @Override
    public String getPublicName() {
        return "SozuriNet";
    }

    @Override
    public ISmsResponse sendSms(String credentials, String phoneNumber, String messageText) {
        try {
            SozuriNetApiCredentials apiCredentials = credentialsService.getCredentials(credentials);
            SozuriNetJsonResponse jsonResponse = apiService.sendSms(apiCredentials, phoneNumber, messageText);

            if (jsonResponse != null) {
                return SozuriNetResponseMapper.mapJsonResponse(jsonResponse);
            } else {
                log.error("Received null response from SozuriNet API");
                return SozuriNetResponseMapper.mapErrorResponse("No response from SMS service");
            }
        } catch (SozuriNetValidationException e) {
            return SozuriNetResponseMapper.mapErrorResponse("Invalid credentials format");
        } catch (HttpStatusIOException e) {
            log.error("HTTP error while sending SMS via SozuriNet: {}", e.getHttpStatusCode(), e);
            return SozuriNetResponseMapper.mapErrorResponse("HTTP error: " + e.getHttpStatusCode());
        } catch (IOException e) {
            log.error("IO error while sending SMS via SozuriNet", e);
            return SozuriNetResponseMapper.mapErrorResponse("Connection error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while sending SMS via SozuriNet", e);
            return SozuriNetResponseMapper.mapErrorResponse("Unexpected error: " + e.getMessage());
        }
    }

}
