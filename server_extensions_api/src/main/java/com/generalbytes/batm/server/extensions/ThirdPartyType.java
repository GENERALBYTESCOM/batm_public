package com.generalbytes.batm.server.extensions;

/**
 * Third party extension type
 */
public enum ThirdPartyType {

    OSW("osw", "Operator`s sample website"),
    MORPHIS("morphis", "Morphis");

    private final String code;
    private final String fullname;

    /**
     * @param code     Third party - extension code
     * @param fullname Third party - extension fullname
     */
    ThirdPartyType(String code, String fullname) {
        this.code = code;
        this.fullname = fullname;
    }

    public String getCode() {
        return code;
    }

    public String getFullname() {
        return fullname;
    }

}

