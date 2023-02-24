package com.generalbytes.batm.server.extensions.extra.identityverification.onfido;

public class OnfidoReportConstants {
    public static final String CLEAR_CHECK_RESULT = "clear";
    public static final String CONSIDER_CHECK_RESULT = "consider";

    public static final String CLEAR_CHECK_SUBRESULT = "clear";
    public static final String CAUTION_CHECK_SUBRESULT = "caution";
    public static final String SUSPECTED_CHECK_SUBRESULT = "suspected";
    public static final String REJECTED_CHECK_SUBRESULT = "rejected";

    public static final String DOCUMENT_REPORT_TYPE = "document";
    public static final String FACIAL_SIMILARITY_REPORT_TYPE = "facial_similarity_photo";

    public static final String DOCUMENT_CHECK_COMPROMISED = "compromised_document";
    public static final String DOCUMENT_CHECK_POLICE_RECORD = "police_record";
    public static final String DOCUMENT_CHECK_DATA_CONSISTENCY = "data_consistency";
    public static final String DOCUMENT_CHECK_VISUAL_CONSISTENCY = "visual_consistency";
    public static final String DOCUMENT_CHECK_IMAGE_INTEGRITY = "image_integrity";
    public static final String DOCUMENT_CHECK_AGE_VALID = "age_validation";
    public static final String DOCUMENT_CHECK_DATA_VALIDATION = "data_validation";
    public static final String DOCUMENT_CHECK_DATA_COMPARISON = "data_comparison";

    public static final String FACIAL_COMPARISON = "face_comparison";
    public static final String FACIAL_IMAGE_INTEGRITY= "image_integrity";
    public static final String FACIAL_VISUAL_AUTHENTICITY = "visual_authenticy";
}
