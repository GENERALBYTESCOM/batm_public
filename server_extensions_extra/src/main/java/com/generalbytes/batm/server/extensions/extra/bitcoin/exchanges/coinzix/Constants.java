package com.generalbytes.batm.server.extensions.extra.bitcoin.exchanges.coinzix;

public abstract class Constants {

    public static abstract class SIDE {
        public static int BUY = 0;
        public static int SELL = 1;
    }

    public static abstract class ORDER_TYPE {
        public static int LIMIT = 0;
        public static int MARKET = 1;
    }

    public static abstract class STATUS {
        public static int CREATE = 0;
        public static int ACTIVE = 1;
        public static int DONE = 2;
        public static int CLOSED = 3;
    }

}
