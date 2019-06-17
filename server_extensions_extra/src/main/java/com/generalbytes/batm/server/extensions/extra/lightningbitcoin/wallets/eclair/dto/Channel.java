package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto;

import java.math.BigInteger;

public class Channel {
    public String nodeId;
    public String channelId;
    public String state;
    public Data data;

    public class Data {
        public Commitments commitments;

        public class Commitments {
            public LocalCommit localCommit;

            public class LocalCommit {
                public Spec spec;

                public class Spec {
                    public BigInteger toLocalMsat;
                }
            }
        }
    }

}
