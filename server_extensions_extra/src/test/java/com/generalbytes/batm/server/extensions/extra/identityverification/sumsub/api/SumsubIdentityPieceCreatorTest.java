package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api;

import com.generalbytes.batm.server.extensions.IIdentityPiece;
import com.generalbytes.batm.server.extensions.extra.identityverification.identitypiece.IdScanIdentityPiece;
import com.generalbytes.batm.server.extensions.extra.identityverification.identitypiece.SelfieIdentityPiece;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.SumSubDocumentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SumsubIdentityPieceCreatorTest {

    private final SumsubIdentityPieceCreator creator = new SumsubIdentityPieceCreator();

    @ParameterizedTest
    @MethodSource("provideMappableDocumentTypes")
    void isMappableDocumentTypeReturnsTrueForMappableTypes(SumSubDocumentType documentType) {
        assertTrue(SumsubIdentityPieceCreator.isMappableDocumentType(documentType));
    }

    private static Stream<Arguments> provideMappableDocumentTypes() {
        return Stream.of(
            Arguments.of(SumSubDocumentType.ID_CARD),
            Arguments.of(SumSubDocumentType.PASSPORT),
            Arguments.of(SumSubDocumentType.DRIVERS),
            Arguments.of(SumSubDocumentType.SELFIE),
            Arguments.of(SumSubDocumentType.VIDEO_SELFIE)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUnmappableDocumentTypes")
    void isMappableDocumentTypeReturnsFalseForUnmappableTypes(SumSubDocumentType documentType) {
        assertFalse(SumsubIdentityPieceCreator.isMappableDocumentType(documentType));
    }

    private static Stream<Arguments> provideUnmappableDocumentTypes() {
        return Stream.of(
            Arguments.of(SumSubDocumentType.UTILITY_BILL),
            Arguments.of(SumSubDocumentType.PROFILE_IMAGE),
            Arguments.of(SumSubDocumentType.RESIDENCE_PERMIT),
            Arguments.of(SumSubDocumentType.OTHER)
        );
    }

    @ParameterizedTest
    @MethodSource("provideIdScanDocumentTypes")
    void createIdentityPieceReturnsIdScanForIdCardPassportDrivers(SumSubDocumentType documentType) {
        byte[] content = "image-data".getBytes();
        String contentType = "image/jpeg";

        IIdentityPiece piece = creator.createIdentityPiece(documentType, contentType, content);

        assertInstanceOf(IdScanIdentityPiece.class, piece);
        assertEquals(contentType, piece.getMimeType());
        assertArrayEquals(content, piece.getData());
    }

    private static Stream<Arguments> provideIdScanDocumentTypes() {
        return Stream.of(
            Arguments.of(SumSubDocumentType.ID_CARD),
            Arguments.of(SumSubDocumentType.PASSPORT),
            Arguments.of(SumSubDocumentType.DRIVERS)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSelfieDocumentTypes")
    void createIdentityPieceReturnsSelfieForSelfieVideoSelfie(SumSubDocumentType documentType) {
        byte[] content = "selfie-data".getBytes();
        String contentType = "image/png";

        IIdentityPiece piece = creator.createIdentityPiece(documentType, contentType, content);

        assertInstanceOf(SelfieIdentityPiece.class, piece);
        assertEquals(contentType, piece.getMimeType());
        assertArrayEquals(content, piece.getData());
    }

    private static Stream<Arguments> provideSelfieDocumentTypes() {
        return Stream.of(
            Arguments.of(SumSubDocumentType.SELFIE),
            Arguments.of(SumSubDocumentType.VIDEO_SELFIE)
        );
    }

    @Test
    void createIdentityPieceThrowsForUnmappableType() {
        byte[] content = "data".getBytes();
        String contentType = "image/jpeg";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> creator.createIdentityPiece(SumSubDocumentType.UTILITY_BILL, contentType, content));

        assertTrue(exception.getMessage().contains("Unmappable document type"));
        assertTrue(exception.getMessage().contains("UTILITY_BILL"));
    }

    @ParameterizedTest
    @EnumSource(SumSubDocumentType.class)
    void createIdentityPieceHandlesMappableDocumentTypes(SumSubDocumentType documentType) {
        Executable createIdentityPieces = () -> creator.createIdentityPiece(documentType, "", "data".getBytes());
        if (SumsubIdentityPieceCreator.isMappableDocumentType(documentType)) {
            assertDoesNotThrow(() -> createIdentityPieces, "Update mapping logic if this fails - failed for: " + documentType);
        } else {
            assertThrows(IllegalArgumentException.class, createIdentityPieces, "Update mappable type list if this fails - failed for: " + documentType);
        }
    }
}
