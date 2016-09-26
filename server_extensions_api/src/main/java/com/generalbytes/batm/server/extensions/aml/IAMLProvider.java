package com.generalbytes.batm.server.extensions.aml;

import com.generalbytes.batm.server.extensions.Contact;

/**
 *  Anti-Money-Laundering Provider.
 */
public interface IAMLProvider {

    /**
     * @return List of country codes supported by provider. Codes are in ISO 3166-1 alpha-2 format (2 digits).
     */
    String[] getSupportedCountries();

    /**
     * @param phoneNumberInternational Phone number in international format. (It begins with the country dialing code, for example "+1" for North America.)
     * @return Contact information about phone number holder.
     */
    Contact getContactByPhoneNumber(String phoneNumberInternational);
}
