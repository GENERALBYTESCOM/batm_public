package com.generalbytes.batm.server.extensions;

public interface ICustomString {

    /**
     * @return Name of custom string.
     */
    default String getName() {
        return null;
    }

    /**
     * @return Value of custom string.
     */
    default String getValue() {
        return null;
    }

    /**
     * @return Language in the ISO standard (for example 'en' for English, 'de' for German, 'de_CH' for Swiss German etc.).
     */
    default String getLanguage() {
        return null;
    }

}
