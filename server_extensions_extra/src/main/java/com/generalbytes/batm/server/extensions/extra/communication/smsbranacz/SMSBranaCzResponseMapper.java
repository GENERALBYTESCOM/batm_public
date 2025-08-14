package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;

import com.generalbytes.batm.server.extensions.communication.ISmsResponse;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * A utility class responsible for mapping responses from the SMSBrána.cz API
 * to the {@link ISmsResponse} interface. Provides methods to parse both
 * error and success responses from the API and transform them into objects
 * that comply with the application's response model.
 */
@Slf4j
@UtilityClass
public class SMSBranaCzResponseMapper {

    public static ISmsResponse mapErrorResponse(String errorMessage) {
        return new SmsResponse(null, ISmsResponse.ResponseStatus.ERROR, null, errorMessage);
    }

    /**
     * Parse the JAXB XML response from SMSBrana API
     * XML response format (automatically parsed by JAXB):
     * <result>
     * <err>0</err>
     * <price>1.1</price>
     * <sms_count>1</sms_count>
     * <credit>1523.32</credit>
     * <sms_id>377351</sms_id>
     * </result>
     */
    public static ISmsResponse mapXmlResponse(SMSBranaCZXmlResponse xmlResponse) {
        Integer errorCode = xmlResponse.getErr();
        if (isErrorResponse(errorCode)) {
            String errorMessage = getErrorMessage(errorCode);
            log.warn("SMS sending failed with error code {}: {}", errorCode, errorMessage);
            return mapErrorResponse(errorMessage);
        }

        Long smsId = xmlResponse.getSmsId();
        if (smsId != null) {
            BigDecimal price = getPrice(xmlResponse);
            log.debug("SMS sent successfully with ID: {}, price: {}", smsId, price);
            return mapSuccessResponse(smsId.toString(), price);
        } else {
            log.warn("Failed to parse SMS ID from successful response: {}", xmlResponse);
            return mapErrorResponse("Invalid response format - missing SMS ID");
        }
    }

    private static boolean isErrorResponse(Integer errorCode) {
        return errorCode != null && errorCode != 0;
    }

    private static BigDecimal getPrice(SMSBranaCZXmlResponse xmlResponse) {
        String priceStr = xmlResponse.getPrice();
        if (StringUtils.isNotBlank(priceStr)) {
            try {
                return new BigDecimal(priceStr.trim());
            } catch (NumberFormatException e) {
                log.warn("Failed to parse price value: {}", priceStr);
            }
        }
        return null;
    }

    private static ISmsResponse mapSuccessResponse(String sid, BigDecimal price) {
        return new SmsResponse(sid, ISmsResponse.ResponseStatus.OK, price, null);
    }

    /**
     * Map error codes to human-readable messages based on SMSBrana API documentation
     */
    private static String getErrorMessage(Integer errorCode) {
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
}
