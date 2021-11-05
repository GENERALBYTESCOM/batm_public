package com.generalbytes.batm.server.extensions;

/**
 * Third party api access type
 */
public enum ApiAccessType {

    OSW("Operator`s sample website"),
    MORPHIS("Morphis");

    private final String apiAccessName;

    ApiAccessType(String apiAccessName) {
        this.apiAccessName = apiAccessName;
    }

}

