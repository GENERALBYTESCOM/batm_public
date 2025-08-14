package com.generalbytes.batm.server.extensions.extra.communication.smsbranacz;

import lombok.extern.slf4j.Slf4j;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Service responsible for managing and validating credentials used to interact with the SMSBrána.cz API.
 * This class processes raw credentials and converts them into an {@link SMSBranaCZApiCredentials} object containing
 * authentication details required by the API.
 * Credentials format: "login:password"
 * - login: SMS Connect username from SMSbrána.cz portal
 * - password: SMS Connect password from SMSbrána.cz portal
 */
@Slf4j
public class SMSBranaCZCredentialsService {

    public SMSBranaCZApiCredentials getCredentials(String credentials) {
        String[] tokens = credentials.split(":");
        if (tokens.length != 2) {
            log.error("Invalid credentials format. Expected format: 'login:password'");
            throw new SMSBranaCZValidationException("Invalid credentials format");
        }

        String login = tokens[0];
        String password = tokens[1];

        String time = LocalDateTime.now(ZoneId.of("Europe/Prague")).format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss"));
        String salt = UUID.randomUUID().toString();
        String auth = generateMD5Hash(password + time + salt);

        return new SMSBranaCZApiCredentials(login, salt, time, auth);
    }

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
            throw new SMSBranaCZValidationException("MD5 algorithm not available");
        }
    }
}
