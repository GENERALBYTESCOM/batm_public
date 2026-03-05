package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.aml.verification.ApplicantCheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.CheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.DocumentType;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantAddress;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantDocument;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantInfo;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantInfoResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantReviewResult;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantReviewedWebhook;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.InspectImageReviewResult;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.InspectionImage;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.InspectionInfoResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.ReviewAnswer;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.ReviewRejectType;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.enums.SumSubDocumentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SumSubApplicantReviewedResultMapperTests {

    private static final String INSPECTION_ID = "inspectionId";
    private static final String APPLICANT_ID = "applicantId";
    SumSubApplicantReviewedResultMapper resultMapper = new SumSubApplicantReviewedResultMapper();

    static Object[] applicantReviewedResultMappingSource() {
        return new Object[]{
                new Object[]{ReviewAnswer.GREEN, null, CheckResult.CLEAR},
                new Object[]{ReviewAnswer.RED, ReviewRejectType.FINAL, CheckResult.REJECTED},
                new Object[]{ReviewAnswer.RED, ReviewRejectType.RETRY, CheckResult.RESUBMISSION_REQUESTED}
        };
    }

    @ParameterizedTest
    @MethodSource("applicantReviewedResultMappingSource")
    void testApplicantReviewedResultMappingRetry(ReviewAnswer answer, ReviewRejectType rejectType, CheckResult result) {

        ApplicantReviewResult reviewResult = mock(ApplicantReviewResult.class);
        when(reviewResult.getReviewAnswer()).thenReturn(answer);
        when(reviewResult.getReviewRejectType()).thenReturn(rejectType);

        assertEquals(result, resultMapper.mapCheckResult(reviewResult));
    }

    @Test
    void testDocumentTypeMapping() {
        assertEquals(DocumentType.driving_licence, resultMapper.translateSSDocumentType(SumSubDocumentType.DRIVERS));
        assertEquals(DocumentType.passport, resultMapper.translateSSDocumentType(SumSubDocumentType.PASSPORT));
        assertEquals(DocumentType.residence_permit, resultMapper.translateSSDocumentType(SumSubDocumentType.RESIDENCE_PERMIT));
        assertEquals(DocumentType.national_identity_card, resultMapper.translateSSDocumentType(SumSubDocumentType.ID_CARD));

        // others
        assertEquals(DocumentType.other, resultMapper.translateSSDocumentType(SumSubDocumentType.VEHICLE_REGISTRATION_CERTIFICATE));
        assertEquals(DocumentType.other, resultMapper.translateSSDocumentType(SumSubDocumentType.AGREEMENT));
        assertEquals(DocumentType.other, resultMapper.translateSSDocumentType(SumSubDocumentType.OTHER));
    }

    @Test
    void testMapResult_responseInfoNull() {
        ApplicantReviewedWebhook applicantReviewedWebhook = mock(ApplicantReviewedWebhook.class);
        when(applicantReviewedWebhook.getInspectionId()).thenReturn(INSPECTION_ID);
        when(applicantReviewedWebhook.getApplicantId()).thenReturn(APPLICANT_ID);
        ApplicantReviewResult result = mock(ApplicantReviewResult.class);
        when(result.getReviewAnswer()).thenReturn(ReviewAnswer.GREEN);
        when(applicantReviewedWebhook.getReviewResult()).thenReturn(result);

        ApplicantInfoResponse applicantInfoResponse = mock(ApplicantInfoResponse.class);

        ApplicantCheckResult checkResult = resultMapper.mapResult(applicantReviewedWebhook, applicantInfoResponse, null);

        assertEquals(INSPECTION_ID, checkResult.getCheckId());
        assertEquals(APPLICANT_ID, checkResult.getIdentityApplicantId());
        assertEquals(CheckResult.CLEAR, checkResult.getResult());

        assertNull(checkResult.getFirstName());
        assertNull(checkResult.getLastName());
        assertNull(checkResult.getBirthDate());
        assertNull(checkResult.getDocumentType());
        assertNull(checkResult.getDocumentNumber());
        assertNull(checkResult.getExpirationDate());
        assertNull(checkResult.getCountry());
        assertNull(checkResult.getRawAddress());
        assertNull(checkResult.getStreetAddress());
        assertNull(checkResult.getCity());
        assertNull(checkResult.getZip());
        assertNull(checkResult.getState());
    }

    @Test
    void testMapResult_documentAndAddressNull() {
        ApplicantReviewedWebhook applicantReviewedWebhook = mock(ApplicantReviewedWebhook.class);
        when(applicantReviewedWebhook.getInspectionId()).thenReturn(INSPECTION_ID);
        when(applicantReviewedWebhook.getApplicantId()).thenReturn(APPLICANT_ID);
        ApplicantReviewResult result = mock(ApplicantReviewResult.class);
        when(result.getReviewAnswer()).thenReturn(ReviewAnswer.GREEN);
        when(applicantReviewedWebhook.getReviewResult()).thenReturn(result);

        ApplicantInfoResponse applicantInfoResponse = mock(ApplicantInfoResponse.class);
        ApplicantInfo applicantInfo = createApplicantInfo();
        when(applicantInfoResponse.getInfo()).thenReturn(applicantInfo);

        ApplicantCheckResult checkResult = resultMapper.mapResult(applicantReviewedWebhook, applicantInfoResponse, mock(InspectionInfoResponse.class));

        assertEquals(INSPECTION_ID, checkResult.getCheckId());
        assertEquals(APPLICANT_ID, checkResult.getIdentityApplicantId());
        assertEquals(CheckResult.CLEAR, checkResult.getResult());

        assertEquals("firstName", checkResult.getFirstName());
        assertEquals("lastName", checkResult.getLastName());
        assertEquals(getTestDate(), checkResult.getBirthDate());
        assertNull(checkResult.getDocumentType());
        assertNull(checkResult.getDocumentNumber());
        assertNull(checkResult.getExpirationDate());
        assertNull(checkResult.getCountry());
        assertNull(checkResult.getRawAddress());
        assertNull(checkResult.getStreetAddress());
        assertNull(checkResult.getCity());
        assertNull(checkResult.getZip());
        assertNull(checkResult.getState());
    }

    @Test
    void testMapResult_testAddress() {
        ApplicantReviewedWebhook applicantReviewedWebhook = mock(ApplicantReviewedWebhook.class);
        ApplicantReviewResult result = mock(ApplicantReviewResult.class);
        when(result.getReviewAnswer()).thenReturn(ReviewAnswer.GREEN);
        when(applicantReviewedWebhook.getReviewResult()).thenReturn(result);

        ApplicantInfoResponse applicantInfoResponse = mock(ApplicantInfoResponse.class);
        ApplicantInfo applicantInfo = createApplicantInfo();
        ApplicantAddress applicantAddress = createApplicantAddress();
        when(applicantInfo.getAddresses()).thenReturn(List.of(applicantAddress));

        when(applicantInfoResponse.getInfo()).thenReturn(applicantInfo);

        ApplicantCheckResult checkResult = resultMapper.mapResult(applicantReviewedWebhook, applicantInfoResponse, mock(InspectionInfoResponse.class));

        assertEquals("country", checkResult.getCountry());
        assertEquals("formattedAddress", checkResult.getRawAddress());
        assertEquals("street", checkResult.getStreetAddress());
        assertEquals("town", checkResult.getCity());
        assertEquals("postCode", checkResult.getZip());
        assertEquals("state", checkResult.getState());
    }

    static Object[] identityDocumentMappingSource() {
        return new Object[]{
                new Object[]{DocumentType.national_identity_card, SumSubDocumentType.ID_CARD},
                new Object[]{DocumentType.passport, SumSubDocumentType.PASSPORT},
                new Object[]{DocumentType.driving_licence, SumSubDocumentType.DRIVERS},
                new Object[]{DocumentType.residence_permit, SumSubDocumentType.RESIDENCE_PERMIT},
        };
    }

    @ParameterizedTest
    @MethodSource("identityDocumentMappingSource")
    void testMapResult_testDocument(DocumentType documentType, SumSubDocumentType sumSubDocumentType) {
        ApplicantReviewedWebhook applicantReviewedWebhook = mock(ApplicantReviewedWebhook.class);
        ApplicantReviewResult result = mock(ApplicantReviewResult.class);
        when(result.getReviewAnswer()).thenReturn(ReviewAnswer.GREEN);
        when(applicantReviewedWebhook.getReviewResult()).thenReturn(result);

        ApplicantInfoResponse applicantInfoResponse = mock(ApplicantInfoResponse.class);
        ApplicantInfo applicantInfo = createApplicantInfo();
        ApplicantDocument applicantDocument = createApplicantDocument(sumSubDocumentType);
        when(applicantInfo.getIdDocs()).thenReturn(List.of(applicantDocument));
        when(applicantInfoResponse.getInfo()).thenReturn(applicantInfo);

        InspectionInfoResponse inspectionInfoResponse = mock(InspectionInfoResponse.class);
        InspectionImage inspectionImage = createInspectionImage(ReviewAnswer.GREEN, sumSubDocumentType);
        when(inspectionInfoResponse.getImages()).thenReturn(List.of(inspectionImage));

        ApplicantCheckResult checkResult = resultMapper.mapResult(applicantReviewedWebhook, applicantInfoResponse, inspectionInfoResponse);

        assertEquals(documentType, checkResult.getDocumentType());
        assertEquals("documentNumber", checkResult.getDocumentNumber());
        assertEquals(getTestDate(), checkResult.getExpirationDate());
        assertEquals("countryFromDocument", checkResult.getCountry());
    }

    @Test
    void testMapResult_testDocumentInvalid() {
        ApplicantReviewedWebhook applicantReviewedWebhook = mock(ApplicantReviewedWebhook.class);
        ApplicantReviewResult result = mock(ApplicantReviewResult.class);
        when(result.getReviewAnswer()).thenReturn(ReviewAnswer.GREEN);
        when(applicantReviewedWebhook.getReviewResult()).thenReturn(result);

        ApplicantInfoResponse applicantInfoResponse = mock(ApplicantInfoResponse.class);
        ApplicantInfo applicantInfo = createApplicantInfo();
        ApplicantDocument applicantDocument = createApplicantDocument(SumSubDocumentType.PASSPORT);
        when(applicantInfo.getIdDocs()).thenReturn(List.of(applicantDocument));
        when(applicantInfoResponse.getInfo()).thenReturn(applicantInfo);

        InspectionInfoResponse inspectionInfoResponse = mock(InspectionInfoResponse.class);
        InspectionImage inspectionImage = createInspectionImage(ReviewAnswer.RED, SumSubDocumentType.PASSPORT);
        when(inspectionInfoResponse.getImages()).thenReturn(List.of(inspectionImage));

        ApplicantCheckResult checkResult = resultMapper.mapResult(applicantReviewedWebhook, applicantInfoResponse, inspectionInfoResponse);

        assertNull(checkResult.getDocumentType());
        assertNull(checkResult.getDocumentNumber());
        assertNull(checkResult.getExpirationDate());
        assertNull(checkResult.getCountry());
    }

    @ParameterizedTest
    @EnumSource(value = SumSubDocumentType.class, names = {"ID_CARD", "PASSPORT", "DRIVERS", "RESIDENCE_PERMIT"}, mode = EnumSource.Mode.EXCLUDE)
    void testMapResult_testDocumentNotIdentityDocument(SumSubDocumentType documentType) {
        ApplicantReviewedWebhook applicantReviewedWebhook = mock(ApplicantReviewedWebhook.class);
        ApplicantReviewResult result = mock(ApplicantReviewResult.class);
        when(result.getReviewAnswer()).thenReturn(ReviewAnswer.GREEN);
        when(applicantReviewedWebhook.getReviewResult()).thenReturn(result);

        ApplicantInfoResponse applicantInfoResponse = mock(ApplicantInfoResponse.class);
        ApplicantInfo applicantInfo = createApplicantInfo();
        ApplicantDocument applicantDocument = createApplicantDocument(SumSubDocumentType.PASSPORT);
        when(applicantInfo.getIdDocs()).thenReturn(List.of(applicantDocument));
        when(applicantInfoResponse.getInfo()).thenReturn(applicantInfo);

        InspectionInfoResponse inspectionInfoResponse = mock(InspectionInfoResponse.class);
        InspectionImage inspectionImage = createInspectionImage(ReviewAnswer.GREEN, documentType);
        when(inspectionInfoResponse.getImages()).thenReturn(List.of(inspectionImage));

        ApplicantCheckResult checkResult = resultMapper.mapResult(applicantReviewedWebhook, applicantInfoResponse, inspectionInfoResponse);

        assertNull(checkResult.getDocumentType());
        assertNull(checkResult.getDocumentNumber());
        assertNull(checkResult.getExpirationDate());
        assertNull(checkResult.getCountry());
    }

    private ApplicantInfo createApplicantInfo() {
        ApplicantInfo info = mock(ApplicantInfo.class);
        when(info.getFirstName()).thenReturn("firstName");
        when(info.getLastName()).thenReturn("lastName");
        when(info.getDob()).thenReturn(LocalDate.of(2000, Month.APRIL, 1));

        return info;
    }

    private ApplicantAddress createApplicantAddress() {
        ApplicantAddress address = mock(ApplicantAddress.class);
        when(address.getFormattedAddress()).thenReturn("formattedAddress");
        when(address.getStreet()).thenReturn("street");
        when(address.getTown()).thenReturn("town");
        when(address.getPostCode()).thenReturn("postCode");
        when(address.getState()).thenReturn("state");
        when(address.getCountry()).thenReturn("country");
        return address;
    }

    private ApplicantDocument createApplicantDocument(SumSubDocumentType documentType) {
        ApplicantDocument document = mock(ApplicantDocument.class);
        when(document.getIdDocType()).thenReturn(documentType);
        when(document.getNumber()).thenReturn("documentNumber");
        when(document.getValidUntil()).thenReturn(LocalDate.of(2000, Month.APRIL, 1));
        when(document.getCountry()).thenReturn("countryFromDocument");
        return document;
    }

    private InspectionImage createInspectionImage(ReviewAnswer documentReviewAnswer, SumSubDocumentType documentType) {
        InspectionImage inspectionImage = mock(InspectionImage.class);
        InspectImageReviewResult inspectImageReviewResult = createInspectImageReviewResult(documentReviewAnswer);
        when(inspectionImage.getReviewResult()).thenReturn(inspectImageReviewResult);
        ApplicantDocument applicantDocument = createApplicantDocument(documentType);
        when(inspectionImage.getIdDocDef()).thenReturn(applicantDocument);
        return inspectionImage;
    }

    private InspectImageReviewResult createInspectImageReviewResult(ReviewAnswer documentReviewAnswer) {
        InspectImageReviewResult result = mock(InspectImageReviewResult.class);
        when(result.getReviewAnswer()).thenReturn(documentReviewAnswer);
        return result;
    }

    public static Date getTestDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault().getId()));
        try {
            return sdf.parse("2000-04-01");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
