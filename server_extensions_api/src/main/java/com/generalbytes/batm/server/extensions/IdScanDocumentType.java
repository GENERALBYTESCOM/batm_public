package com.generalbytes.batm.server.extensions;

/**
 * Identifies the specific side or part of a document.
 *
 * <p>Document sides are grouped as follows:</p>
 * <ul>
 *     <li>{@link #ID_CARD_FRONT} and {@link #ID_CARD_BACK}
 *     – may also be replaced by {@link #ID_CARD_SINGLE_SIDE} if only one side is available.</li>
 *     <li>{@link #FIRST_DOCUMENT_FRONT}, {@link #FIRST_DOCUMENT_BACK}, {@link #SECOND_DOCUMENT_FRONT}, and {@link #SECOND_DOCUMENT_BACK}
 *     – all four sides should always be provided together.</li>
 * </ul>
 */
public enum IdScanDocumentType {
    /**
     * Single side of the primary ID card (used when only one side is available).
     * Part of the "ID Card One Side Only" AML/KYC instruction.
     */
    ID_CARD_SINGLE_SIDE,
    /**
     * Front side of the primary ID card.
     * Part of the "ID Card Both Sides" AML/KYC instruction.
     *
     * <p>Related: {@link #ID_CARD_BACK}</p>
     */
    ID_CARD_FRONT,
    /**
     * Backside of the primary ID card.
     * Part of the "ID Card Both Sides" AML/KYC instruction.
     *
     * <p>Related: {@link #ID_CARD_FRONT}</p>
     */
    ID_CARD_BACK,
    /**
     * Front side of the first supporting document.
     * Part of the "Two Documents" AML/KYC instruction.
     *
     * <p>Related: {@link #FIRST_DOCUMENT_BACK}, {@link #SECOND_DOCUMENT_FRONT}, {@link #SECOND_DOCUMENT_BACK}</p>
     */
    FIRST_DOCUMENT_FRONT,
    /**
     * Backside of the first supporting document.
     * Part of the "Two Documents" AML/KYC instruction.
     *
     * <p>Related: {@link #FIRST_DOCUMENT_FRONT}, {@link #SECOND_DOCUMENT_FRONT}, {@link #SECOND_DOCUMENT_BACK}</p>
     */
    FIRST_DOCUMENT_BACK,
    /**
     * Front side of the second supporting document.
     * Part of the "Two Documents" AML/KYC instruction.
     *
     * <p>Related: {@link #FIRST_DOCUMENT_FRONT}, {@link #FIRST_DOCUMENT_BACK}, {@link #SECOND_DOCUMENT_BACK}</p>
     */
    SECOND_DOCUMENT_FRONT,
    /**
     * Backside of the second supporting document.
     * Part of the "Two Documents" AML/KYC instruction.
     *
     * <p>Related: {@link #FIRST_DOCUMENT_FRONT}, {@link #FIRST_DOCUMENT_BACK}, {@link #SECOND_DOCUMENT_FRONT}</p>
     */
    SECOND_DOCUMENT_BACK
}
