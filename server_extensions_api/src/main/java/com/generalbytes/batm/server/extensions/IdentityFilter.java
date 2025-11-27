package com.generalbytes.batm.server.extensions;

import java.time.LocalDate;

/**
 * Filter for searching for identities. Null values are ignored.
 *
 * @param givenName      First name of the identity. Case in-sensitive match
 * @param surname        Last name of the identity. Case in-sensitive match.
 * @param dateOfBirth    Date of birth of the identity.
 * @param documentNumber Document number. e.g., passport number or ID card number. Case-sensitive match.
 * @param ssn            Social Security Number. Case-sensitive match.
 */
public record IdentityFilter(String givenName,
                             String surname,
                             LocalDate dateOfBirth,
                             String documentNumber,
                             String ssn) {
}
