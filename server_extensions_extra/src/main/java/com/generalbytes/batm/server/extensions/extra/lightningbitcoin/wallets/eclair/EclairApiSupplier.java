package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.eclair;;

@FunctionalInterface
public interface EclairApiSupplier<T> {

    T get() throws Exception;
}
