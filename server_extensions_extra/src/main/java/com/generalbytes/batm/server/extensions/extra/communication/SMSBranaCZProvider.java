package com.generalbytes.batm.server.extensions.extra.communication;

import com.generalbytes.batm.server.extensions.communication.ICommunicationProvider;
import com.generalbytes.batm.server.extensions.communication.ISmsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * SMS provider implementation for SMSbrána.cz service.
 * Uses the SMS Connect API for sending SMS messages with secure authentication.
 * Features:
 * - Secure MD5 hash-based authentication with timestamp and salt
 * - Automatic phone number formatting for Czech numbers
 * - Text message cleaning (removes non-ASCII characters)
 * - JAXB XML response parsing with proper annotations
 * - Comprehensive error handling with descriptive error messages
 * - Full API error code mapping
 * Credentials format: "login:password"
 * - login: SMS Connect username from SMSbrána.cz portal
 * - password: SMS Connect password from SMSbrána.cz portal
 * Setup:
 * 1. Register at <a href="https://www.smsbrana.cz/">here</a>
 * 2. Activate SMS Connect service in portal settings
 * 3. Configure allowed IP addresses (optional but recommended)
 * 4. Use login:password format in BATM SMS provider configuration
 * API Documentation: <a href="https://portal.smsbrana.cz/dokumenty/smsconnect_dokumentace_cz_revison2.pdf">here</a>
 */
public class SMSBranaCZProvider implements ICommunicationProvider {
    private static final Logger log = LoggerFactory.getLogger("batm.server.extensions.extra.communication.SMSBranaCZProvider");

    private static final String API_BASE_URL = "https://api.smsbrana.cz/smsconnect";

    @Override
    public String getName() {
        return "SMSBrana.cz";
    }

    @Override
    public String getPublicName() {
        return "SMSBrana.CZ";
    }

    @Override
    public ISmsResponse sendSms(String credentials, String phoneNumber, String messageText) {
        String[] tokens = credentials.split(":");
        if (tokens.length != 2) {
            log.error("Invalid credentials format. Expected format: 'login:password'");
            return new SmsResponseImpl(null, ISmsResponse.ResponseStatus.ERROR, null,
                new SmsErrorResponseImpl("Invalid credentials format"));
        }

        String login = tokens[0];
        String password = tokens[1];

        try {
            ISMSBranaCZAPI api = RestProxyFactory.createProxy(ISMSBranaCZAPI.class, API_BASE_URL);

            // Generate time and salt for secure authentication
            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
            String salt = UUID.randomUUID().toString().substring(0, 8);
            String auth = generateMD5Hash(password + time + salt);

            // Clean phone number - remove spaces and ensure proper format
            String cleanPhoneNumber = phoneNumber.replaceAll("\\s+", "");
            if (!cleanPhoneNumber.startsWith("+") && !cleanPhoneNumber.startsWith("00")) {
                // If it's a Czech number without international prefix, add +420
                if (cleanPhoneNumber.length() == 9 && cleanPhoneNumber.matches("\\d{9}")) {
                    cleanPhoneNumber = "+420" + cleanPhoneNumber;
                }
            }

            // Remove non-ASCII characters from message text as per API requirements
            String cleanMessage = messageText.replaceAll("[^\\x00-\\x7F]", "");

            String xml = api.sendSms(
                login,
                time,
                salt,
                auth,
                "send_sms",
                cleanPhoneNumber,
                cleanMessage
            );

            if (xml != null) {
                JAXBContext jaxbContext = JAXBContext.newInstance(SMSBranaCZXmlResponse.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

                StringReader reader = new StringReader(xml);
                SMSBranaCZXmlResponse xmlResponse = (SMSBranaCZXmlResponse) unmarshaller.unmarshal(reader);

                return parseXmlResponse(xmlResponse);
            } else {
                log.error("Received null response from SMSBrana API");
                return new SmsResponseImpl(null, ISmsResponse.ResponseStatus.ERROR, null,
                    new SmsErrorResponseImpl("No response from SMS service"));
            }

        } catch (HttpStatusIOException e) {
            log.error("HTTP error while sending SMS via SMSBrana: " + e.getHttpStatusCode(), e);
            return new SmsResponseImpl(null, ISmsResponse.ResponseStatus.ERROR, null,
                new SmsErrorResponseImpl("HTTP error: " + e.getHttpStatusCode()));
        } catch (IOException e) {
            log.error("IO error while sending SMS via SMSBrana", e);
            return new SmsResponseImpl(null, ISmsResponse.ResponseStatus.ERROR, null,
                new SmsErrorResponseImpl("Connection error: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while sending SMS via SMSBrana", e);
            return new SmsResponseImpl(null, ISmsResponse.ResponseStatus.ERROR, null,
                new SmsErrorResponseImpl("Unexpected error: " + e.getMessage()));
        }
    }

    /**
     * Parse the JAXB XML response from SMSBrana API
     * XML response format (automatically parsed by JAXB):
     * <result>
     *   <err>0</err>
     *   <price>1.1</price>
     *   <sms_count>1</sms_count>
     *   <credit>1523.32</credit>
     *   <sms_id>377351</sms_id>
     * </result>
     */
    private ISmsResponse parseXmlResponse(SMSBranaCZXmlResponse xmlResponse) {
        log.debug("Parsing SMSBrana XML response: {}", xmlResponse);

        // Check if it's an error response (err != 0)
        Integer errorCode = xmlResponse.getErr();
        if (errorCode != null && errorCode != 0) {
            String errorMessage = getErrorMessage(errorCode);
            log.warn("SMS sending failed with error code {}: {}", errorCode, errorMessage);
            return new SmsResponseImpl(
                null,
                ISmsResponse.ResponseStatus.ERROR,
                null,
                new SmsErrorResponseImpl(errorMessage)
            );
        }

        // Parse success response
        Long smsId = xmlResponse.getSmsId();
        BigDecimal price = null;

        // Parse price if available
        String priceStr = xmlResponse.getPrice();
        if (priceStr != null && !priceStr.trim().isEmpty()) {
            try {
                price = new BigDecimal(priceStr);
            } catch (NumberFormatException e) {
                log.warn("Failed to parse price value: {}", priceStr);
            }
        }

        // Log credit information for debugging
        String credit = xmlResponse.getCredit();
        if (credit != null) {
            log.debug("Remaining credit: {}", credit);
        }

        // Log SMS count for debugging
        Integer smsCount = xmlResponse.getSmsCount();
        if (smsCount != null) {
            log.debug("SMS count: {}", smsCount);
        }

        if (smsId != null) {
            log.info("SMS sent successfully with ID: {}, price: {}", smsId, price);
            return new SmsResponseImpl(
                smsId.toString(),
                ISmsResponse.ResponseStatus.OK,
                price,
                null
            );
        } else {
            log.error("Failed to parse SMS ID from successful response: {}", xmlResponse);
            return new SmsResponseImpl(null, ISmsResponse.ResponseStatus.ERROR, null,
                new SmsErrorResponseImpl("Invalid response format - missing SMS ID"));
        }
    }

    /**
     * Generate MD5 hash for authentication as required by SMSBrana API
     */
    private String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    /**
     * Map error codes to human-readable messages based on SMSBrana API documentation
     */
    private String getErrorMessage(Integer errorCode) {
        if (errorCode == null) {
            return "Unknown error";
        }

        return switch (errorCode) {
            case -1 -> "Duplicate user_id - SMS with same ID was already sent";
            case 1 -> "Unknown error";
            case 2 -> "Invalid login";
            case 3 -> "Invalid password or hash";
            case 4 -> "Invalid time - time difference between servers too large";
            case 5 -> "Forbidden IP address";
            case 6 -> "Invalid action name";
            case 7 -> "Salt already used today";
            case 8 -> "Database connection failed";
            case 9 -> "Insufficient credit";
            case 10 -> "Invalid phone number";
            case 11 -> "Empty message text";
            case 12 -> "Message too long (max 459 characters)";
            default -> "Error code: " + errorCode;
        };
    }

//    /**
//     * Main method for testing the SMSBranaCZProvider
//     * Sends a test SMS "Ahoj" to phone number 603572526
//     */
//    public static void main(String[] args) {
//        System.out.println("Testing SMSBranaCZProvider...");
//
//        // Create provider instance
//        SMSBranaCZProvider provider = new SMSBranaCZProvider();
//
//        // Test credentials
//        String credentials = "username:pass";
//        String phoneNumber = "603572526";
//        String message = "Ahoj";
//
//        System.out.println("Provider: " + provider.getName());
//        System.out.println("Sending SMS to: " + phoneNumber);
//        System.out.println("Message: " + message);
//        System.out.println("Credentials: " + credentials.split(":")[0] + ":****");
//        System.out.println();
//
//        try {
//            // Send SMS
//            ISmsResponse response = provider.sendSms(credentials, phoneNumber, message);
//
//            // Display results
//            System.out.println("=== SMS RESPONSE ===");
//            System.out.println("Status: " + response.getStatus());
//            System.out.println("SMS ID: " + response.getSid());
//            System.out.println("Price: " + response.getPrice());
//
//            if (response.getErrorResponse() != null) {
//                System.out.println("Error Message: " + response.getErrorResponse().getErrorMessage());
//                System.out.println("Is Blacklisted: " + response.getErrorResponse().isBlacklisted());
//            }
//
//            if (response.getStatus() == ISmsResponse.ResponseStatus.OK) {
//                System.out.println("\n✅ SMS sent successfully!");
//            } else {
//                System.out.println("\n❌ SMS sending failed!");
//            }
//
//        } catch (Exception e) {
//            System.err.println("❌ Exception occurred while sending SMS:");
//            e.printStackTrace();
//        }
//
//        System.out.println("\nTest completed.");
//    }
}
