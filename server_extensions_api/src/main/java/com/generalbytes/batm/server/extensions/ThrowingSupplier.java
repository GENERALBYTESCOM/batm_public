package com.generalbytes.batm.server.extensions;

@FunctionalInterface
public interface ThrowingSupplier<T> {

    T get() throws Exception;
}
