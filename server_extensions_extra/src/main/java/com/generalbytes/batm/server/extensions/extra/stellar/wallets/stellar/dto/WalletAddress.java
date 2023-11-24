package com.generalbytes.batm.server.extensions.extra.stellar.wallets.stellar.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public class WalletAddress {
	private String id;
    private State state;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public enum State {
        USED("used"),
        UNUSED("unused");

        private final String value;

        State(String value) {
            this.value = value;
        }

        @JsonValue
        public String getValue() {
            return value;
        }
    }
}
