package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IIdentityPiece;
import com.generalbytes.batm.server.extensions.extra.identityverification.identitypiece.IdScanIdentityPiece;
import com.generalbytes.batm.server.extensions.extra.identityverification.identitypiece.SelfieIdentityPiece;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.DocumentDefinition;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.InspectionImage;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.SumSubDocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SumsubDocumentDownloaderTest {

    private SumsubDocumentDownloader downloader;
    private SumsubDocumentClient client;
    private SumsubIdentityPieceCreator creator;
    private IExtensionContext extensionContext;

    @BeforeEach
    void setUp() {
        extensionContext = mock(IExtensionContext.class);
        client = mock(SumsubDocumentClient.class);
        creator = new SumsubIdentityPieceCreator();
        downloader = new SumsubDocumentDownloader(client, creator, 3, 0);
    }

    @Test
    void downloadAndStoreDocumentsHappyScenario() throws IOException {
        String identityPublicId = "user123";
        String inspectionId = "insp456";

        InspectionImage img1 = createMockImage(101, SumSubDocumentType.ID_CARD);
        InspectionImage img2 = createMockImage(102, SumSubDocumentType.SELFIE);
        List<InspectionImage> images = Arrays.asList(img1, img2);

        byte[] content1 = "image1".getBytes();
        byte[] content2 = "image2".getBytes();

        when(client.downloadDocument(inspectionId, "101"))
            .thenReturn(new DownloadedDocument(content1, "image/jpeg"));
        when(client.downloadDocument(inspectionId, "102"))
            .thenReturn(new DownloadedDocument(content2, "image/png"));

        downloader.downloadAndStoreDocuments(identityPublicId, inspectionId, images, extensionContext);

        ArgumentCaptor<IIdentityPiece> pieceCaptor = ArgumentCaptor.forClass(IIdentityPiece.class);
        verify(extensionContext, times(2)).addIdentityPiece(eq(identityPublicId), pieceCaptor.capture());

        List<IIdentityPiece> capturedPieces = pieceCaptor.getAllValues();
        assertInstanceOf(IdScanIdentityPiece.class, capturedPieces.get(0));
        assertEquals("image/jpeg", capturedPieces.get(0).getMimeType());
        assertArrayEquals(content1, capturedPieces.get(0).getData());

        assertInstanceOf(SelfieIdentityPiece.class, capturedPieces.get(1));
        assertEquals("image/png", capturedPieces.get(1).getMimeType());
        assertArrayEquals(content2, capturedPieces.get(1).getData());
    }

    @Test
    void downloadAndStoreDocumentsSkipsUnmappableAndNulls() throws IOException {
        String identityPublicId = "user123";
        String inspectionId = "insp456";

        InspectionImage mappable = createMockImage(101, SumSubDocumentType.ID_CARD);
        InspectionImage unmappable = createMockImage(102, SumSubDocumentType.UTILITY_BILL);
        InspectionImage nullType = createMockImage(103, null);
        InspectionImage nullId = new InspectionImage();

        List<InspectionImage> images = Arrays.asList(mappable, unmappable, nullType, nullId);
        when(client.downloadDocument(anyString(), anyString()))
            .thenReturn(new DownloadedDocument("data".getBytes(), "image/jpeg"));

        downloader.downloadAndStoreDocuments(identityPublicId, inspectionId, images, extensionContext);

        verify(client, times(1)).downloadDocument(inspectionId, "101");
        verify(client, never()).downloadDocument(inspectionId, "102");
        verify(client, never()).downloadDocument(inspectionId, "103");
        verify(extensionContext, times(1)).addIdentityPiece(eq(identityPublicId), any());
    }

    @Test
    void downloadAndStoreDocumentsHandlesEmptyOrNullList() {
        downloader.downloadAndStoreDocuments("user1", "insp1", Collections.emptyList(), extensionContext);
        downloader.downloadAndStoreDocuments("user1", "insp1", null, extensionContext);

        verify(extensionContext, never()).addIdentityPiece(anyString(), any());
    }

    @Test
    void downloadAndStoreDocumentsRetriesOnFailure() throws IOException {
        String identityPublicId = "user123";
        String inspectionId = "insp456";
        InspectionImage img = createMockImage(101, SumSubDocumentType.ID_CARD);

        doThrow(new IOException("Timeout"))
            .doReturn(new DownloadedDocument("data".getBytes(), "image/jpeg"))
            .when(client).downloadDocument(inspectionId, "101");

        downloader.downloadAndStoreDocuments(identityPublicId, inspectionId, Collections.singletonList(img), extensionContext);

        verify(client, times(2)).downloadDocument(inspectionId, "101");
        verify(extensionContext, times(1)).addIdentityPiece(eq(identityPublicId), any());
    }

    @Test
    void downloadAndStoreDocumentsPersistentFailure() throws IOException {
        String identityPublicId = "user123";
        String inspectionId = "insp456";
        int maxRetries = 2;
        downloader = new SumsubDocumentDownloader(client, creator, maxRetries, 0);

        InspectionImage img = createMockImage(101, SumSubDocumentType.ID_CARD);

        doThrow(new IOException("Persistent error"))
            .when(client).downloadDocument(inspectionId, "101");

        downloader.downloadAndStoreDocuments(identityPublicId, inspectionId, Collections.singletonList(img), extensionContext);

        verify(client, times(maxRetries)).downloadDocument(inspectionId, "101");
        verify(extensionContext, never()).addIdentityPiece(anyString(), any());
    }

    @Test
    void downloadAndStoreDocumentsHandlesNotOkResponse() throws IOException {
        String identityPublicId = "user123";
        String inspectionId = "insp456";
        InspectionImage img = createMockImage(101, SumSubDocumentType.ID_CARD);

        String errorMessage = "Error downloading document 101: 404: {\"error\":\"Not found\"}";
        doThrow(new IOException(errorMessage))
            .when(client).downloadDocument(inspectionId, "101");

        downloader.downloadAndStoreDocuments(identityPublicId, inspectionId, Collections.singletonList(img), extensionContext);

        verify(client, times(3)).downloadDocument(inspectionId, "101");
        verify(extensionContext, never()).addIdentityPiece(anyString(), any());
    }

    private InspectionImage createMockImage(Integer imageId, SumSubDocumentType docType) {
        InspectionImage img = mock(InspectionImage.class);
        when(img.getImageId()).thenReturn(imageId);

        if (docType != null) {
            DocumentDefinition def = mock(DocumentDefinition.class);
            when(def.getIdDocType()).thenReturn(docType);
            when(img.getIdDocDef()).thenReturn(def);
        } else {
            DocumentDefinition def = mock(DocumentDefinition.class);
            when(def.getIdDocType()).thenReturn(null);
            when(img.getIdDocDef()).thenReturn(def);
        }

        return img;
    }
}
