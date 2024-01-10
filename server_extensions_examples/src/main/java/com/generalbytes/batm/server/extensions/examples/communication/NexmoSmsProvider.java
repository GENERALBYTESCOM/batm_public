package com.generalbytes.batm.server.extensions.examples.communication;

import com.generalbytes.batm.server.extensions.communication.ICommunicationProvider;
import com.generalbytes.batm.server.extensions.communication.ISmsResponse;
import com.nexmo.client.NexmoClient;
import com.nexmo.client.sms.MessageStatus;
import com.nexmo.client.sms.SmsSubmissionResponse;
import com.nexmo.client.sms.SmsSubmissionResponseMessage;
import com.nexmo.client.sms.messages.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NexmoSmsProvider implements ICommunicationProvider {
    private static final Logger log = LoggerFactory.getLogger("batm.server.extensions.extra.examples.communication.NexmoSmsProvider");

    @Override
    public String getName() {
        return "Nexmo";
    }

    @Override
    public ISmsResponse sendSms(String credentials, String phoneNumber, String messageText) {
        String[] tokens = credentials.split(":");
        String apiKey = tokens[0];
        String apiSecret = tokens[1];
        String from = tokens[2];

        try {
            NexmoClient client = new NexmoClient.Builder()
                .apiKey(apiKey)
                .apiSecret(apiSecret)
                .build();

            TextMessage message = new TextMessage(from, phoneNumber, messageText);

            SmsSubmissionResponse response = client.getSmsClient().submitMessage(message);

            if (response != null && response.getMessageCount() > 0) {
                SmsSubmissionResponseMessage responseMessage = response.getMessages().get(0);

                responseMessage.getId();
                responseMessage.getMessagePrice();
                responseMessage.getStatus();
                responseMessage.getErrorText();

                if (MessageStatus.OK == responseMessage.getStatus()) {
                    return new SmsResponseImpl(responseMessage.getId(), ISmsResponse.ResponseStatus.OK, responseMessage.getMessagePrice(), null);
                } else {
                    return new SmsResponseImpl(responseMessage.getId(), ISmsResponse.ResponseStatus.ERROR, null,
                        new SmsErrorResponseImpl("Error while sending SMS: " + responseMessage.getStatus().name() + " - " + responseMessage.getErrorText()));
                }
            }
        } catch (Throwable e) {
            log.error("sendSMS By Nexmo - Error.", e);
        }

        return new SmsResponseImpl(null, ISmsResponse.ResponseStatus.ERROR, null, new SmsErrorResponseImpl("Error while sending SMS"));
    }

}
