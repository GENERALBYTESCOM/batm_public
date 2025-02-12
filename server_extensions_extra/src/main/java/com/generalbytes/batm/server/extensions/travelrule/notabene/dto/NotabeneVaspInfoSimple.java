package com.generalbytes.batm.server.extensions.travelrule.notabene.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Holds information about a single VASP.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotabeneVaspInfoSimple {

    /**
     * Decentralized Identifier
     *
     * @see <a href="https://devx.notabene.id/docs/decentralized-identifiers-dids">Notabene Documentation</a>
     */
    private String did;
    private String name;
    private String website;
    private String logo;
    private String incorporationCountry;
    private String documents;
    private boolean hasAdmin;
    private boolean isNotifiable;
    private boolean isActiveSender;
    private boolean isActiveReceiver;
    private String issuers;

}
