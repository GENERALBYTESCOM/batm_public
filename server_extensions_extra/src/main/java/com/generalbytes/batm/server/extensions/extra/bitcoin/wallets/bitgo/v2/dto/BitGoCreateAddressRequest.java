package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto;

public class BitGoCreateAddressRequest {
    private Integer chain;
    private String label;
    private Boolean lowPriority;
    private String gasPrice;

    public Integer getChain() {
        return chain;
    }

    /**
     * Enum:0 1 10 11 20 21
     * https://github.com/BitGo/unspents/blob/master/src/codes.ts
     */
    public void setChain(Integer chain) {
        this.chain = chain;
    }

    public String getLabel() {
        return label;
    }

    /**
     * <= 250 characters; A human-readable label which should be applied to the new address
     */
    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getLowPriority() {
        return lowPriority;
    }

    /**
     * Whether the deployment of the address forwarder contract should use a low priority fee key (ETH only)
     */
    public void setLowPriority(Boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    /**
     * Explicit gas price to use when deploying the forwarder contract (ETH only). If not given, defaults to the current estimated network gas price.
     */
    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }
}
