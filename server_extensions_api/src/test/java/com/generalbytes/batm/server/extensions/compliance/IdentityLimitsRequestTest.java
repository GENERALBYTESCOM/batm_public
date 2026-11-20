package com.generalbytes.batm.server.extensions.compliance;

import com.generalbytes.batm.server.extensions.TransactionPaymentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdentityLimitsRequestTest {

    private static final String FIAT_CURRENCY = "EUR";
    private static final String TERMINAL_SERIAL_NUMBER = "BT100001";
    private static final String IDENTITY_PUBLIC_ID = "IE3BVEBUIIXZ3SZV";

    @Test
    void forComplianceTierShouldKeepEveryValue() {
        IdentityLimitsRequest request = IdentityLimitsRequest.forComplianceTier(
                FIAT_CURRENCY, TERMINAL_SERIAL_NUMBER, IDENTITY_PUBLIC_ID, 4L, TransactionPaymentType.PAYMENT_CARD);

        assertEquals(FIAT_CURRENCY, request.getFiatCurrency());
        assertEquals(TERMINAL_SERIAL_NUMBER, request.getTerminalSerialNumber());
        assertEquals(IDENTITY_PUBLIC_ID, request.getIdentityPublicId());
        assertEquals(4L, request.getComplianceTierId());
        assertEquals(TransactionPaymentType.PAYMENT_CARD, request.getPaymentType());
    }

    @Test
    void forComplianceTierShouldRejectMissingPaymentType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> IdentityLimitsRequest.forComplianceTier(
                        FIAT_CURRENCY, TERMINAL_SERIAL_NUMBER, IDENTITY_PUBLIC_ID, 4L, null));

        assertEquals("paymentType is required", exception.getMessage());
    }

    @Test
    void forAmlKycShouldCarryNoComplianceTierOrPaymentType() {
        IdentityLimitsRequest request =
                IdentityLimitsRequest.forAmlKyc(FIAT_CURRENCY, TERMINAL_SERIAL_NUMBER, IDENTITY_PUBLIC_ID);

        assertEquals(FIAT_CURRENCY, request.getFiatCurrency());
        assertEquals(TERMINAL_SERIAL_NUMBER, request.getTerminalSerialNumber());
        assertEquals(IDENTITY_PUBLIC_ID, request.getIdentityPublicId());
        assertNull(request.getComplianceTierId());
        assertNull(request.getPaymentType());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectMissingFiatCurrency(String fiatCurrency) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> IdentityLimitsRequest.forAmlKyc(fiatCurrency, TERMINAL_SERIAL_NUMBER, IDENTITY_PUBLIC_ID));

        assertEquals("fiatCurrency is required", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectMissingTerminalSerialNumber(String terminalSerialNumber) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> IdentityLimitsRequest.forAmlKyc(FIAT_CURRENCY, terminalSerialNumber, IDENTITY_PUBLIC_ID));

        assertEquals("terminalSerialNumber is required", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void shouldRejectMissingIdentityPublicId(String identityPublicId) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> IdentityLimitsRequest.forComplianceTier(
                        FIAT_CURRENCY, TERMINAL_SERIAL_NUMBER, identityPublicId, 4L, TransactionPaymentType.CASH));

        assertEquals("identityPublicId is required", exception.getMessage());
    }

    @Test
    void shouldBeEqualWhenEveryValueMatches() {
        IdentityLimitsRequest request = IdentityLimitsRequest.forComplianceTier(
                FIAT_CURRENCY, TERMINAL_SERIAL_NUMBER, IDENTITY_PUBLIC_ID, 4L, TransactionPaymentType.CASH);
        IdentityLimitsRequest same = IdentityLimitsRequest.forComplianceTier(
                FIAT_CURRENCY, TERMINAL_SERIAL_NUMBER, IDENTITY_PUBLIC_ID, 4L, TransactionPaymentType.CASH);

        assertEquals(request, same);
        assertEquals(request.hashCode(), same.hashCode());
    }

    @Test
    void shouldNotBeEqualWhenTheTierDiffers() {
        IdentityLimitsRequest request = IdentityLimitsRequest.forComplianceTier(
                FIAT_CURRENCY, TERMINAL_SERIAL_NUMBER, IDENTITY_PUBLIC_ID, 4L, TransactionPaymentType.CASH);
        IdentityLimitsRequest otherTier = IdentityLimitsRequest.forComplianceTier(
                FIAT_CURRENCY, TERMINAL_SERIAL_NUMBER, IDENTITY_PUBLIC_ID, 5L, TransactionPaymentType.CASH);

        assertNotEquals(request, otherTier);
    }

    @Test
    void shouldNotBeEqualToAnAmlKycRequestWithTheSameIdentifiers() {
        IdentityLimitsRequest complianceRequest = IdentityLimitsRequest.forComplianceTier(
                FIAT_CURRENCY, TERMINAL_SERIAL_NUMBER, IDENTITY_PUBLIC_ID, 4L, TransactionPaymentType.CASH);
        IdentityLimitsRequest amlKycRequest =
                IdentityLimitsRequest.forAmlKyc(FIAT_CURRENCY, TERMINAL_SERIAL_NUMBER, IDENTITY_PUBLIC_ID);

        assertNotEquals(complianceRequest, amlKycRequest);
    }
}
