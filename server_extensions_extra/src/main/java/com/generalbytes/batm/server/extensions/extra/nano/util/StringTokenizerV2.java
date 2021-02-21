package com.generalbytes.batm.server.extensions.extra.nano.util;

/** Tokenizer allowing empty values and skipping. */
public class StringTokenizerV2 {

    private final String[] tokens;
    private int tokenIndex = 0;

    public StringTokenizerV2(String[] tokens) {
        this.tokens = tokens;
    }


    public String next() {
        return tokens[tokenIndex++];
    }

    public boolean hasNext() {
        return tokens.length > tokenIndex;
    }

    public void skip(int n) {
        tokenIndex += n;
    }

}
