package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request to get ownership information about a customer blockchain address.
 *
 * @see <a href="https://devx.notabene.id/reference/addressownershipget">Notabene Documentation</a>
 */
@Getter
@Setter
@NoArgsConstructor
public class NotabeneAddressOwnershipInfoRequest {

    private String address;
    private String vaspDid;
    private String asset;

}
