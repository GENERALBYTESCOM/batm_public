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
package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto;

import com.generalbytes.batm.server.extensions.ILightningChannel;

public class Channel implements ILightningChannel {
    public String nodeId;
    public String channelId;
    public String state;
    public Data data;
    private String remoteNodeAlias;
    private String localNodeAlias;

    public class Data {
        public Commitments commitments;
        public String shortChannelId;

        public class Commitments {
            public LocalCommit localCommit;

            public class LocalCommit {
                public Spec spec;

                public class Spec {
                    public Long toLocalMsat;
                    public Long toRemoteMsat;
                }
            }

            public CommitInput commitInput;

            public class CommitInput {
                public String outPoint;
                public Long amountSatoshis;
            }

            public LocalParams localParams;

            public class LocalParams {
                public String nodeId;
                public boolean isFunder;
            }
        }
    }

    @Override
    public String getShortChannelId() {
        return data.shortChannelId;
    }

    @Override
    public boolean isLocalFunder() {
        return data.commitments.localParams.isFunder;
    }

    @Override
    public boolean isOnline() {
        return "NORMAL".equals(state);
    }


    public String getRemoteNodeId() {
        return nodeId;
    }

    public String getLocalNodeId() {
        return data.commitments.localParams.nodeId;
    }

    @Override
    public String getRemoteNodeAlias() {
        return remoteNodeAlias;
    }

    @Override
    public String getLocalNodeAlias() {
        return localNodeAlias;
    }

    public long getCapacityMsat() {
        return data.commitments.commitInput.amountSatoshis * 1000;
    }

    @Override
    public long getBalanceMsat() {
        return data.commitments.localCommit.spec.toLocalMsat;
    }


    public void setRemoteNodeAlias(String remoteNodeAlias) {
        this.remoteNodeAlias = remoteNodeAlias;
    }

    public void setLocalNodeAlias(String localNodeAlias) {
        this.localNodeAlias = localNodeAlias;
    }
}
