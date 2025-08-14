package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;

import com.generalbytes.batm.server.extensions.communication.ICommunicationProvider;
import com.generalbytes.batm.server.extensions.communication.ISmsResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import si.mazi.rescu.HttpStatusIOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;

/**
 * Provider class for sending SMS using SMSBrána.cz communication service.
 * This class implements the {@link ICommunicationProvider} interface and integrates with the
 * SMSBrána.cz API. It handles authentication, sending SMS, and processing API responses.
 * <p>
 * Setup:
 * 1. Register at <a href="https://www.smsbrana.cz/">here</a>
 * 2. Activate SMS Connect service in portal settings
 * 3. Configure allowed IP addresses (optional but recommended)
 * 4. Use login:password format in BATM SMS provider configuration
 * API Documentation: <a href="https://portal.smsbrana.cz/dokumenty/smsconnect_dokumentace_cz_revison2.pdf">here</a>
 */
@Slf4j
@AllArgsConstructor
public class SMSBranaCZProvider implements ICommunicationProvider {
    private final SMSBranaCZApiService apiService;
    private final SMSBranaCZCredentialsService credentialsService;

    @Override
    public String getName() {
        return "SMSBrana.cz";
    }

    @Override
    public String getPublicName() {
        return "SMSBrana.cz";
    }

    @Override
    public ISmsResponse sendSms(String credentials, String phoneNumber, String messageText) {
        try {
            SMSBranaCZApiCredentials apiCredentials = credentialsService.getCredentials(credentials);
            String rawResponse = apiService.sendSms(apiCredentials, phoneNumber, messageText);

            if (rawResponse != null) {
                SMSBranaCZXmlResponse xmlResponse = unmarshallRawResponse(rawResponse);
                logStatistics(xmlResponse);
                return SMSBranaCzResponseMapper.mapXmlResponse(xmlResponse);
            } else {
                log.error("Received null response from SMSBrana API");
                return SMSBranaCzResponseMapper.mapErrorResponse("No response from SMS service");
            }
        } catch (SMSBranaCZValidationException e) {
            return SMSBranaCzResponseMapper.mapErrorResponse("Invalid credentials format");
        } catch (HttpStatusIOException e) {
            log.error("HTTP error while sending SMS via SMSBrana: {}", e.getHttpStatusCode(), e);
            return SMSBranaCzResponseMapper.mapErrorResponse("HTTP error: " + e.getHttpStatusCode());
        } catch (IOException e) {
            log.error("IO error while sending SMS via SMSBrana", e);
            return SMSBranaCzResponseMapper.mapErrorResponse("Connection error: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while sending SMS via SMSBrana", e);
            return SMSBranaCzResponseMapper.mapErrorResponse("Unexpected error: " + e.getMessage());
        }
    }

    private SMSBranaCZXmlResponse unmarshallRawResponse(String rawResponse) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(SMSBranaCZXmlResponse.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        StringReader reader = new StringReader(rawResponse);
        return (SMSBranaCZXmlResponse) unmarshaller.unmarshal(reader);
    }

    private void logStatistics(SMSBranaCZXmlResponse xmlResponse) {
        log.debug("Remaining credit: {}, SMS count: {}", xmlResponse.getCredit(), xmlResponse.getSmsCount());
    }

}