package com.generalbytes.batm.server.extensions.travelrule.notabene;

import lombok.Data;

@Data
public class NotabeneConfiguration {
    private String apiUrl;
    private String authApiUrl;
    private boolean automaticApprovalOfOutgoingTransfersEnabled;
    private String masterExtensionsUrl;
}
