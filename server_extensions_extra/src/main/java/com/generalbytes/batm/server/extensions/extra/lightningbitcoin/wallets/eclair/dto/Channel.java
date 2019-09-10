package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair.dto;

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
                    public Long toLocalMsat;
                    public Long toRemoteMsat;
                }
            }

            public CommitInput commitInput;

            public class CommitInput {
                public String outPoint;
                public Long amountSatoshis;
            }
        }
    }

}
