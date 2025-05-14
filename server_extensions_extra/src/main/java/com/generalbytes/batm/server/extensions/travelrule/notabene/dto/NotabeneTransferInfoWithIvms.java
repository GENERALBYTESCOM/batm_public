package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Holds information about a transfer.
 *
 * <p>This is a part of the response by several endpoints
 * and webhook payloads.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotabeneTransferInfoWithIvms extends NotabeneTransferInfo {
    private NotabeneIvms ivms101;
}