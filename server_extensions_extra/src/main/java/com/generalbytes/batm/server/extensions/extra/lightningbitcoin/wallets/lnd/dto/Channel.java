/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.generalbytes.batm.server.extensions.ILightningChannel;

public class Channel implements ILightningChannel {
    @JsonProperty("chan_id")
    private String chanId;
    @JsonProperty("remote_pubkey")
    private String remotePubkey;
    @JsonProperty("initiator")
    private boolean initiator;
    @JsonProperty("local_balance")
    private long localBalanceSat;
    @JsonProperty("capacity")
    private long capacitySat;
    @JsonProperty("chan_status_flags")
    private String channelStatusFlags;

    private String remoteNodeAlias;
    private String localNodeAlias;


    @Override
    public String getShortChannelId() {
        return chanId;
    }

    @Override
    public boolean isOnline() {
        return "ChanStatusDefault".equals(channelStatusFlags);
    }

    @Override
    public String getRemoteNodeId() {
        return remotePubkey;
    }

    @Override
    public String getRemoteNodeAlias() {
        return remoteNodeAlias;
    }

    @Override
    public String getLocalNodeId() {
        return null;
    }

    @Override
    public String getLocalNodeAlias() {
        return localNodeAlias;
    }

    @Override
    public boolean isLocalFunder() {
        return initiator;
    }

    @Override
    public long getBalanceMsat() {
        return localBalanceSat * 1000;
    }

    @Override
    public long getCapacityMsat() {
        return capacitySat * 1000;
    }

    public void setRemoteNodeAlias(String remoteNodeAlias) {
        this.remoteNodeAlias = remoteNodeAlias;
    }

    public void setLocalNodeAlias(String localNodeAlias) {
        this.localNodeAlias = localNodeAlias;
    }

}
