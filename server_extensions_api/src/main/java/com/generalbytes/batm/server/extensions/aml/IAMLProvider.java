package com.generalbytes.batm.server.extensions.aml;

import com.generalbytes.batm.server.extensions.Contact;
import com.generalbytes.batm.server.extensions.Country;

/**
 *  Anti-Money-Laundering Provider.
 */
public interface IAMLProvider {

    /**
     * @return List of contries supported by provider.
     */
    Country[] getSupportedCountries();

    /**
     * @param phoneNumber Complete phone number, beginning with the country dialing code (for example, “1” or "+1" for North America).
     * @return Information about phone number holder.
     */
    Contact getContactByPhoneNumber(String phoneNumber);


    /**
     * @param phoneNumber Complete phone number, beginning with the country dialing code (for example, “1” or "+1" for North America).
     * @return
     */
    boolean isPhoneNumberSupported(String phoneNumber);
}
