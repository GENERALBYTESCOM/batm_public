package com.generalbytes.batm.server.extensions.aml;

import com.generalbytes.batm.server.extensions.Contact;

import java.math.BigDecimal;

public interface IContactResponse {

    /**
     * Contact information.
     */
    Contact getContact();

    /**
     * Price of checking contact information.
     */
    BigDecimal getPrice();
}
