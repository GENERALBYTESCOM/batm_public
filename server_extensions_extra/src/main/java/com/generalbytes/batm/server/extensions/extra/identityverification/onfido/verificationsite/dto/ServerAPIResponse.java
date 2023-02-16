package com.generalbytes.batm.server.extensions.extra.identityverification.onfido.verificationsite.dto;

public class ServerAPIResponse {
    public boolean success;
    public String message;

    public static ServerAPIResponse success(String message) {
        ServerAPIResponse result = new ServerAPIResponse();
        result.message = message;
        result.success = true;
        return result;
    }

    public static ServerAPIResponse failure(String message) {
        ServerAPIResponse result = new ServerAPIResponse();
        result.message = message;
        result.success = false;
        return result;
    }
}