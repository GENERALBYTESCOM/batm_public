package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Enum representing various types of documents supported by Sumsub for verification purposes.
 *
 * <p><a href="https://docs.sumsub.com/reference/add-verification-documents#supported-document-types">Supported document types</a>
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
    COVID_VACCINATION_FORM("COVID vaccination document (may contain the QR code)"),
    ARBITRARY_DOC("Any document that contains information valuable for you. For example, an employment contract, lease agreement, non-disclosure agreement (NDA), service agreement, construction contract, and so on"),
    EXTERNAL_DB_DOC("Documents retrieved from the external database"),
    FILE_ATTACHMENT("File attachment"),
    IDENTITY_VIDEO("Video recording of the seamless identity document capture process"),
    PAYMENT_SOURCE("Entity confirming payment (like bank card, crypto wallet, etc)"),
    SIGNED_CONTRACT("A signed contract"),
    UTILITY_BILL2("Second proof of address document"),
    VISA("A visa document"),
    COMPANY_DOC("Default type of the document for company verification"),
    DIRECTORS_REGISTRY("Directors registry"),
    GOOD_STANDING_CERT("Certificate of good standing"),
    INCORPORATION_ARTICLES("Memorandum/articles of incorporation/association/registration"),
    INCORPORATION_CERT("Certificate of incorporation/registration"),
    INCUMBENCY_CERT("Certificate of incumbency"),
    INFORMATION_STATEMENT("Statement of information"),
    PARTNERSHIP_AGREEMENT("Partnership agreement"),
    POWER_OF_ATTORNEY("Power of attorney"),
    PROOF_OF_ADDRESS("Proof of address. For example, a utility bill, rent contract or an electricity bill"),
    PROOF_OF_NATURE_OF_BUSINESS("Proof of nature of business"),
    REGULATORY_LICENSE("Regulatory/Operating license"),
    SELF_DECLARATION_FORM("Self-declaration form"),
    SHAREHOLDER_REGISTRY("Shareholder registry"),
    STATE_REGISTRY("Recent excerpt from a state company registry"),
    TRADE_LICENSE("Commercial/Trading license"),
    TRANSPARENCY_REGISTRY_EXTRACT("Recent excerpt from a transparency company registry"),
    TRUST_AGREEMENT("Trust agreement"),
    OTHER("Should be used only when nothing else applies"),
    // no longer in new documentation, but keeping just in case
    BANK_STATEMENT("A bank statement"),
    EMPLOYMENT_CERTIFICATE("A document from an employer, e.g. proof that a user works there"),
    @JsonEnumDefaultValue // Defensive fallback
    UNKNOWN("Unknown document");

    @Getter
    private final String description;

    SumSubDocumentType(String description) {
        this.description = description;
    }

    public static List<SumSubDocumentType> identityDocuments() {
        return Arrays.asList(ID_CARD, PASSPORT, DRIVERS, RESIDENCE_PERMIT);
    }
}
