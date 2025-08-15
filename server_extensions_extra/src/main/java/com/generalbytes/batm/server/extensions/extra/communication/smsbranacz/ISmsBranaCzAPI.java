package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * SMSBrána.cz API interface using si.mazi.rescu
 * API documentation: <a href="https://portal.smsbrana.cz/dokumenty/smsconnect_dokumentace_cz_revison2.pdf">here</a>
 * Note: API returns XML responses in format:
 * <result>
 * <err>0</err>
 * <price>1.1</price>
 * <sms_count>1</sms_count>
 * <credit>1523.32</credit>
 * <sms_id>377351</sms_id>
 * </result>
 */
@Produces(MediaType.TEXT_PLAIN)
@Path("/http.php")
public interface ISmsBranaCzAPI {

    /**
     * Send SMS using SMSBrána.cz API with secure authentication
     *
     * @param login   SMS Connect username
     * @param time    timestamp in format YYYYMMDDTHHMMSS
     * @param salt    random string (max 50 chars), must be unique per day
     * @param auth    MD5 hash of (password + time + salt)
     * @param action  action name, should be "send_sms"
     * @param number  phone number in national or international format
     * @param message SMS text (max 459 chars, no diacritics)
     * @return JAXB XML response object from API
     * @throws IOException if communication fails
     */
    @GET
    String sendSms(
        @QueryParam("login") String login,
        @QueryParam("time") String time,
        @QueryParam("sul") String salt,
        @QueryParam("auth") String auth,
        @QueryParam("action") String action,
        @QueryParam("number") String number,
        @QueryParam("message") String message,
        @QueryParam("data_code") String dataCode
    ) throws IOException;

}