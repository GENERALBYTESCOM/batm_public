package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Enum representing various types of documents supported by Sumsub for verification purposes.
 *
 * <p><a href="https://docs.sumsub.com/reference/add-id-documents#supported-document-types">Supported document types</a>
 */
public enum SumSubDocumentType {

    ID_CARD("An ID card"),
    PASSPORT("A passport"),
    DRIVERS("A driving license"),
    RESIDENCE_PERMIT("Residence permit or registration document in the foreign city/country"),
    UTILITY_BILL("A utility bill"),
    SELFIE("A selfie with a document (in this case no additional metadata should be sent)"),
    VIDEO_SELFIE("A selfie video (can be used in webSDK or mobileSDK)"),
    PROFILE_IMAGE("A profile image, i.e. userpic (in this case no additional metadata should be sent)"),
    ID_DOC_PHOTO("Photo from an ID doc (like a photo from a passport) (No additional metadata should be sent)"),
    AGREEMENT("Agreement of some sort, e.g. for processing personal info"),
    CONTRACT("Some sort of contract"),
    DRIVERS_TRANSLATION("Translation of the driving license required in the target country"),
    INVESTOR_DOC("A document from an investor, e.g. documents which disclose assets of the investor"),
    VEHICLE_REGISTRATION_CERTIFICATE("Certificate of vehicle registration"),
    INCOME_SOURCE("A proof of income"),
    PAYMENT_METHOD("Entity confirming payment (like bank card, crypto wallet, etc)"),
    BANK_CARD("A bank card, like Visa or Maestro"),
    COVID_VACCINATION_FORM("COVID vaccination document (may contain the QR code)."),
    OTHER("Should be used only when nothing else applies"),
    // no longer in new documentation, but keeping just in case
    BANK_STATEMENT("A bank statement"),
    EMPLOYMENT_CERTIFICATE("A document from an employer, e.g. proof that a user works there");

    @Getter
    private final String description;

    SumSubDocumentType(String description) {
        this.description = description;
    }

    public static List<SumSubDocumentType> identityDocuments() {
        return Arrays.asList(ID_CARD, PASSPORT, DRIVERS, RESIDENCE_PERMIT);
    }
}
