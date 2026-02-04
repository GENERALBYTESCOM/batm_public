package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IIdentity;
import com.generalbytes.batm.server.extensions.IIdentityBase;
import com.generalbytes.batm.server.extensions.IIdentityPiece;
import com.generalbytes.batm.server.extensions.aml.verification.ApplicantCheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.CheckResult;
import com.generalbytes.batm.server.extensions.aml.verification.DocumentType;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.SumsubDocumentDownloader;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantInfoResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.BaseWebhookBody;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.CreateIdentityVerificationSessionResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.InspectionInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.InvocationResult;

import jakarta.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SumSubWebhookProcessorTests {

    private static final String ALG_KEY = "HMAC_SHA256_HEX";

    @Mock
    private SumSubApiService apiService;
    private SumSubWebhookParser webhookParser;
    @Mock
    private IExtensionContext ctx;
    @Mock
    private SumsubDocumentDownloader documentDownloader;

    private SumSubWebhookProcessor webhookProcessor;

    @BeforeEach
    void setUp() {
        webhookParser = new SumSubWebhookParser();
        SumSubApplicantReviewedResultMapper resultMapper = new SumSubApplicantReviewedResultMapper();
        webhookProcessor = new SumSubWebhookProcessor(ctx, apiService, webhookParser, resultMapper, "webhooksecret", documentDownloader);
    }

    private ApplicantInfoResponse getApplicantInfoResponseDrivers() throws Exception {
        String bodyString = String.format("""
                {
                  "id": "%s",
                  "createdAt": "2020-06-24 05:05:14",
                  "clientId": "ClientName",
                  "inspectionId": "5b594ade0a975a36c9379e67",
                  "externalUserId": "%s",
                  "info": {
                    "firstName": "CHRISTIAN",
                    "firstNameEn": "CHRISTIAN",
                    "lastName": "SMITH",
                    "lastNameEn": "SMITH",
                    "dob": "1989-07-16",
                    "country": "CAN",
                    "addresses": [
                      {
                        "street": "22 MOUNT STREET",
                        "streetEn": "22 MOUNT STREET",
                        "state": "Ontario",
                        "stateEn": "ON",
                        "town": "TORONTO",
                        "townEn": "TORONTO",
                        "postCode": "M5A1L2",
                        "country": "CAN",
                        "formattedAddress": "22 MOUNT STREET, TORONTO, ON, CANADA, M5A1L2",
                        "metadata": "CAN"
                      }
                    ],
                    "idDocs": [
                            {
                                "idDocType": "PASSPORT",
                                "country": "CAN",
                                "firstName": "CHRISTIAN",
                                "firstNameEn": "CHRISTIAN",
                                "lastName": "SMITH",
                                "lastNameEn": "SMITH",
                                "issuedDate": "1990-01-01",
                                "validUntil": "2030-01-01",
                                "firstIssuedDate": "1990-01-01",
                                "number": "1234567890",
                                "dob": "1989-07-16"
                            }\
                    ]
                  },
                  "agreement": {
                    "createdAt": "2020-06-24 04:18:40",
                    "source": "WebSDK",
                    "targets": [
                      "By clicking Next, I accept [the Terms and Conditions](https://www.sumsub.com/consent-to-personal-data-processing/)",
                      "I agree to the processing of my personal data, as described in [the Consent to Personal Data Processing](https://sumsub.com/consent-to-personal-data-processing/)"
                    ]
                  },
                  "email": "christman1@gmail.com",
                  "applicantPlatform": "Android",
                  "requiredIdDocs": {
                    "docSets": [
                      {
                        "idDocSetType": "IDENTITY",
                        "types": [
                          "PASSPORT",
                          "ID_CARD"
                        ]
                      },
                      {
                        "idDocSetType": "SELFIE",
                        "types": [
                          "SELFIE"
                        ]
                      }
                    ]
                  },
                 "questionnaires": [
                        {
                            "id": "occupation_questionnaire",
                            "sections": {
                                "occupation_section": {
                                    "items": {
                                        "occupation_legal": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_medical": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_transportation": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_engineering": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_information_technology": {
                                            "value": null,
                                            "values": null
                                        },
                                        "industry": {
                                            "value": "sports",
                                            "values": null
                                        },
                                        "occupation_real_estate": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_hospitality": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_trades": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_admin": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_sports": {
                                            "value": "Sports and Leisure Support Activities",
                                            "values": null
                                        },
                                        "occupation_arts": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_education": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_religious_vocation": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_accounting": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_government": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_agriculture": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_military": {
                                            "value": null,
                                            "values": null
                                        },
                                        "occupation_sales": {
                                            "value": null,
                                            "values": null
                                        }
                                    }
                                }
                            }
                        }
                    ]
                  },
                  "review": {
                    "elapsedSincePendingMs": 115879,
                    "elapsedSinceQueuedMs": 95785,
                    "reprocessing": true,
                    "levelName": "basic-kyc",
                    "createDate": "2020-06-24 05:11:02+0000",
                    "reviewDate": "2020-06-24 05:12:58+0000",
                    "startDate": "2020-06-24 05:11:22+0000",
                    "reviewResult": {
                      "reviewAnswer": "GREEN"
                    },
                    "reviewStatus": "completed",
                    "notificationFailureCnt": 0
                  },
                  "lang": "de",
                  "type": "individual"
                }
                """, "5cb56e8e0a975a35f333cb83", "123");

        return webhookParser.parse(bodyString, ApplicantInfoResponse.class);
    }

    private InspectionInfoResponse getInpsectionInfoResponse() throws Exception {
        String bodyString = String.format("""
                {
                    "id": "%s",
                    "inspectionDate": "2024-04-17 20:40:13",
                    "applicantId": "%s",
                    "images": [
                        {
                            "id": "662034208ab1c911cc2618e3",
                            "addedDate": "2024-04-17 20:42:08",
                            "creatorClientId": "WebSDK",
                            "imageHash": "6ced9a9a3c43db870723e04d6c18e931d072fd2a",
                            "imageFileName": "image.jpg",
                            "resizedImageId": 194371722,
                            "mimeType": "jpeg",
                            "sigHash": "9ae39639edbb38eb5600fc82ae05d49f",
                            "imageId": 1375349600,
                            "fileSize": 2834528,
                            "actualResolution": {
                                "width": 3024,
                                "height": 4032
                            },
                            "exifResolution": {
                                "width": 4032,
                                "height": 3024
                            },
                            "answer": "YELLOW",
                            "comments": null,
                            "imageTrust": {
                                "trust": 0.45
                            },
                            "idDocDef": {
                                "country": "CAN",
                                "idDocType": "PASSPORT"
                            },
                            "extractedInfo": {
                                "ocrDocType": "can.passport.type1",
                                "forensicLabels": [
                                    "check_0000000000002",
                                    "check_0000000001003",
                                    "check_0000000000001",
                                    "check_0000000001002",
                                    "check_0000000000011",
                                    "check_0000000001001",
                                    "check_0000000000006",
                                    "check_0000000000005",
                                    "check_0000000001006",
                                    "check_1000000002002",
                                    "check_0000000001005",
                                    "check_1000000002001",
                                    "check_0000000001004",
                                    "check_1000000004001",
                                    "check_1000000005001",
                                    "check_0000000000009",
                                    "check_0000000000008",
                                    "check_0000000000007",
                                    "check_0000000001008"
                                ],
                                "requiredFields": [
                                    "firstName",
                                    "lastName",
                                    "number",
                                    "dob",
                                    "validUntil",
                                    "mrzLine1",
                                    "mrzLine2"
                                ],
                                "screenRecapture": false,
                                "unsatisfactoryAnswer": "GREEN"
                            },
                            "deleted": true,
                            "attemptId": "Nknqb"
                        },
                        {
                            "id": "662034339b66873a08e145e7",
                            "addedDate": "2024-04-17 20:42:27",
                            "creatorClientId": "WebSDK",
                            "imageHash": "854db902879ee4a478a8b3238e86a43c43ede0d8",
                            "imageFileName": "image.jpg",
                            "resizedImageId": 1526322140,
                            "mimeType": "jpeg",
                            "sigHash": "9ae39639edbb38eb5600fc82f1dba571",
                            "imageId": 237263502,
                            "fileSize": 2417268,
                            "actualResolution": {
                                "width": 3024,
                                "height": 4032
                            },
                            "exifResolution": {
                                "width": 4032,
                                "height": 3024
                            },
                            "answer": "YELLOW",
                            "comments": null,
                            "imageTrust": {
                                "trust": 0.45
                            },
                            "idDocDef": {
                                "country": "CAN",
                                "idDocType": "PASSPORT"
                            },
                            "extractedInfo": {
                                "ocrDocType": "can.passport.type1",
                                "forensicLabels": [
                                    "check_0000000000002",
                                    "check_0000000001003",
                                    "check_0000000000001",
                                    "check_0000000001002",
                                    "check_0000000000011",
                                    "check_0000000001001",
                                    "check_0000000000006",
                                    "check_0000000000005",
                                    "check_0000000001006",
                                    "check_1000000002002",
                                    "check_0000000001005",
                                    "check_1000000002001",
                                    "check_0000000001004",
                                    "check_1000000004001",
                                    "check_1000000005001",
                                    "check_0000000000009",
                                    "check_0000000000008",
                                    "check_0000000000007",
                                    "check_0000000001008"
                                ],
                                "requiredFields": [
                                    "firstName",
                                    "lastName",
                                    "number",
                                    "dob",
                                    "validUntil",
                                    "mrzLine1",
                                    "mrzLine2"
                                ],
                                "screenRecapture": false,
                                "unsatisfactoryAnswer": "GREEN"
                            },
                            "reviewResult": {
                                "reviewAnswer": "GREEN"
                            },
                            "attemptId": "Nknqb"
                        },
                        {
                            "id": "662034588137fa35bec68bff",
                            "addedDate": "2024-04-17 20:43:04",
                            "creatorClientId": "WebSDK",
                            "imageHash": "58fb01039d154c8b66de8ba3ca566ec6",
                            "imageFileName": "BLANK FORM.pdf",
                            "resizedImageId": 459198982,
                            "mimeType": "pdf",
                            "mediaType": "application/pdf",
                            "sigHash": "9ae39639edbb38eb5600fc82897dc4c5",
                            "imageId": 1988246330,
                            "fileSize": 105964,
                            "answer": "GREEN",
                            "softwareTag": "Skia/PDF m119 Google Docs Renderer",
                            "comments": null,
                            "imageTrust": {
                                "trust": 0.7
                            },
                            "idDocDef": {
                                "country": "CAN",
                                "idDocType": "UTILITY_BILL"
                            },
                            "extractedInfo": {
                                "pagesCnt": 6
                            },
                            "reviewResult": {
                                "moderationComment": "Provide a valid proof of address. Documents must:\\n- contain your full address\\n- not be older than 3 month(s).",
                                "clientComment": "The proof of address was rejected because:\\n- user's full address is not on the document\\n- document's date of issue is older than 3 month(s).",
                                "reviewAnswer": "RED",
                                "rejectLabels": [
                                    "BAD_PROOF_OF_ADDRESS"
                                ],
                                "reviewRejectType": "RETRY",
                                "buttonIds": [
                                    "proofOfAddress_fullAddress",
                                    "proofOfAddress_issueDate"
                                ]
                            },
                            "deleted": true,
                            "attemptId": "DwjrW"
                        },
                        {
                            "id": "662035c88ab1c911cc266bc2",
                            "addedDate": "2024-04-17 20:49:12",
                            "creatorClientId": "WebSDK",
                            "imageHash": "3b33a06c498d16f28b018c822c3cbac4",
                            "imageFileName": "MyVF Invoice_16 April 2024.pdf",
                            "resizedImageId": 1023575177,
                            "mimeType": "pdf",
                            "mediaType": "application/pdf",
                            "sigHash": "9ae39639edbb38eb5600fc829d2d9af4",
                            "imageId": 1657955595,
                            "fileSize": 433314,
                            "creationDate": "2024-04-16 08:31:57",
                            "modificationDate": "2024-04-17 20:46:13",
                            "answer": "GREEN",
                            "softwareTag": "iText® 5.5.6 ©2000-2015 iText Group NV (AGPL-version)",
                            "comments": null,
                            "imageTrust": {
                                "trust": 0.7
                            },
                            "idDocDef": {
                                "country": "CAN",
                                "idDocType": "UTILITY_BILL"
                            },
                            "extractedInfo": {
                                "pagesCnt": 4
                            },
                            "reviewResult": {
                                "reviewAnswer": "GREEN"
                            },
                            "attemptId": "tmdEN"
                        }
                    ],
                    "checks": [
                        {
                            "answer": "GREEN",
                            "checkType": "BEHAVIORAL_FRAUD",
                            "createdAt": "2024-04-17 20:42:37",
                            "id": "96f6050f-2ad4-40ae-b29f-5d16b3543069",
                            "attemptId": "Nknqb",
                            "behavioralFraudInfo": null
                        },
                        {
                            "answer": "GREEN",
                            "checkType": "CROSS_VALIDATION",
                            "createdAt": "2024-04-17 20:42:37",
                            "id": "910405da-c28b-456b-8265-c96336224734",
                            "attemptId": "Nknqb"
                        },
                        {
                            "answer": "RED",
                            "checkType": "SIMILAR_SEARCH",
                            "createdAt": "2024-04-17 20:42:37",
                            "id": "d0625231-4dd7-4bce-a4fc-73c2a595d990",
                            "attemptId": "Nknqb",
                            "similarSearchInfo": null
                        },
                        {
                            "answer": "RED",
                            "checkType": "SIMILAR_SEARCH",
                            "createdAt": "2024-04-17 20:42:38",
                            "id": "7407d30b-7216-4795-a578-958376c64de8",
                            "attemptId": "Nknqb",
                            "similarSearchInfo": null
                        },
                        {
                            "answer": "RED",
                            "checkType": "AUTO_CHECK",
                            "createdAt": "2024-04-17 20:42:38",
                            "id": "f74f0685-64c5-4943-a279-09ea1839fd84",
                            "attemptId": "Nknqb",
                            "autoCheckInfo": {
                                "executionDotGraph": "digraph G {\\ngraph [splines=curved, nodesep=0.8]\\nnode [fontname = \\"helvetica\\"];\\nPENDING[shape=diamond, id=\\"PENDING\\",color=\\"#3598DC\\",fontcolor=\\"#3598DC\\",style=\\"rounded,filled\\",fillcolor=\\"#CCE5F6\\",fontsize=14,penwidth=1,label=<All docs<br/>submitted<br/>(Auto generated for GB_PANEL_AUS)>]\\n METADATA_PASSPORT[shape=rectangle, id=\\"IMAGE_QUALITY_PASSPORT\\", color=\\"#19BC9B\\", fontcolor=\\"#19BC9B\\", style=\\"rounded,filled\\", fillcolor=\\"#C5EEE6\\", fontsize=14, penwidth=1, label=<Image Editors Check<br/> (passport)>]\\n PENDING -> METADATA_PASSPORT [color=\\"#19BC9B\\"]\\n OCR_PASSPORT[shape=rectangle, id=\\"OCR_PASSPORT\\", color=\\"#19BC9B\\", fontcolor=\\"#19BC9B\\", style=\\"rounded,filled\\", fillcolor=\\"#C5EEE6\\", fontsize=14, penwidth=1, label=<Document Recognition<br/> (passport)>]\\n METADATA_PASSPORT -> OCR_PASSPORT [color=\\"#19BC9B\\"]\\n AUTO_REGULATIONS_PASSPORT[shape=rectangle, id=\\"AUTO_REGULATIONS_PASSPORT\\", color=\\"#19BC9B\\", fontcolor=\\"#19BC9B\\", style=\\"rounded,filled\\", fillcolor=\\"#C5EEE6\\", fontsize=14, penwidth=1, label=<PoI Regulations<br/> (passport)>]\\n OCR_PASSPORT -> AUTO_REGULATIONS_PASSPORT [color=\\"#19BC9B\\"]\\n CROSS_CHECK[shape=rectangle, id=\\"CROSS_CHECK\\", color=\\"#19BC9B\\", fontcolor=\\"#19BC9B\\", style=\\"rounded,filled\\", fillcolor=\\"#C5EEE6\\", fontsize=14, penwidth=1, label=<Cross-check>]\\n OCR_PASSPORT -> CROSS_CHECK [color=\\"#19BC9B\\"]\\n AUTO_REGULATIONS_PASSPORT -> CROSS_CHECK [color=\\"#19BC9B\\"]\\n FRAUD_DUPLICATES[shape=rectangle, id=\\"FRAUD_DUPLICATES\\", color=\\"#19BC9B\\", fontcolor=\\"#19BC9B\\", style=\\"rounded,filled\\", fillcolor=\\"#C5EEE6\\", fontsize=14, penwidth=1, label=<Suspected fraud duplicates>]\\n METADATA_PASSPORT -> FRAUD_DUPLICATES [color=\\"#19BC9B\\"]\\n CROSS_CHECK -> FRAUD_DUPLICATES [color=\\"#19BC9B\\"]\\n SIMILAR_SEARCH[shape=rectangle, id=\\"SIMILAR_SEARCH\\", color=\\"#EF5656\\", fontcolor=\\"#EF5656\\", style=\\"rounded,filled\\", fillcolor=\\"#FBD5D5\\", fontsize=14, penwidth=1, label=<Similar applicants search<br/><FONT POINT-SIZE=\\"6\\">[Duplicate hit detected]</FONT><br/><FONT POINT-SIZE=\\"6\\">duplicates</FONT>>]\\n METADATA_PASSPORT -> SIMILAR_SEARCH [color=\\"#19BC9B\\"]\\n CROSS_CHECK -> SIMILAR_SEARCH [color=\\"#19BC9B\\"]\\n FRAUD_DUPLICATES -> SIMILAR_SEARCH [color=\\"#19BC9B\\"]\\n}",
                                "graphName": "Auto generated for GB_PANEL_AUS",
                                "notFullProcessedImages": null
                            }
                        },
                        {
                            "answer": "RED",
                            "checkType": "POA",
                            "createdAt": "2024-04-17 20:43:48",
                            "id": "5034f2a9-9a62-46e0-a47e-a72b3e76f59c",
                            "attemptId": "DwjrW",
                            "inputDoc": {
                                "idDocType": "UTILITY_BILL",
                                "country": "CAN",
                                "issuedDate": "2023-10-01",
                                "uncertainFields": null
                            },
                            "poaCheckInfo": {
                                "answer": "RED",
                                "unexpired": "RED",
                                "acceptableType": "YELLOW",
                                "validAddress": "RED",
                                "notSameDoc": "GREEN",
                                "poiPoaCountryMatched": null,
                                "acceptableRegion": "YELLOW",
                                "companyContact": null,
                                "poiAsPoa": false,
                                "subType": null,
                                "poaIdDocType": null
                            }
                        },
                        {
                            "answer": "RED",
                            "checkType": "AUTO_CHECK",
                            "createdAt": "2024-04-17 20:43:48",
                            "id": "f1e7ef5a-8128-4d18-a12b-2521b7d69236",
                            "attemptId": "DwjrW",
                            "autoCheckInfo": {
                                "executionDotGraph": "digraph G {\\ngraph [splines=curved, nodesep=0.8]\\nnode [fontname = \\"helvetica\\"];\\nPENDING[shape=diamond, id=\\"PENDING\\",color=\\"#3598DC\\",fontcolor=\\"#3598DC\\",style=\\"rounded,filled\\",fillcolor=\\"#CCE5F6\\",fontsize=14,penwidth=1,label=<All docs<br/>submitted<br/>(Auto generated for POA_non-resident)>]\\n POA_CHECK[shape=rectangle, id=\\"POA_CHECK\\", color=\\"#EF5656\\", fontcolor=\\"#EF5656\\", style=\\"rounded,filled\\", fillcolor=\\"#FBD5D5\\", fontsize=14, penwidth=1, label=<Proof of address<br/><FONT POINT-SIZE=\\"6\\">poaCheck</FONT>>]\\n PENDING -> POA_CHECK [color=\\"#19BC9B\\"]\\n}",
                                "graphName": "Auto generated for POA_non-resident",
                                "notFullProcessedImages": [
                                    1988246330
                                ]
                            }
                        },
                        {
                            "answer": "GREEN",
                            "checkType": "POA",
                            "createdAt": "2024-04-17 20:49:39",
                            "id": "4a698e25-9062-4ed4-81f2-dc0b63d5b2aa",
                            "attemptId": "tmdEN",
                            "inputDoc": {
                                "idDocType": "UTILITY_BILL",
                                "country": "CAN",
                                "firstName": "CHRISTIAN",
                                "firstNameEn": "CHRISTIAN",
                                "issuedDate": "2024-04-16",
                                "address": {
                                    "street": "22 MOUNT STREET",
                                    "streetEn": "22 MOUNT STREET",
                                    "state": "Ontario",
                                    "stateEn": "ON",
                                    "town": "TORONTO",
                                    "townEn": "TORONTO",
                                    "postCode": "M5A1L2",
                                    "country": "CAN",
                                    "formattedAddress": "22 MOUNT STREET, TORONTO, ON, CANADA, M5A1L2",
                                    "metadata": null
                                }
                            },
                            "poaCheckInfo": {
                                "answer": "GREEN",
                                "unexpired": "GREEN",
                                "acceptableType": "GREEN",
                                "validAddress": "GREEN",
                                "notSameDoc": "GREEN",
                                "poiPoaCountryMatched": null,
                                "acceptableRegion": "GREEN",
                                "companyContact": {
                                    "type": "mobileOperator"
                                },
                                "poiAsPoa": false,
                                "subType": null,
                                "poaIdDocType": null
                            }
                        },
                        {
                            "answer": "GREEN",
                            "checkType": "CROSS_VALIDATION",
                            "createdAt": "2024-04-17 20:49:47",
                            "id": "addfcc29-b1f4-4fa2-a489-147f7624fd0a",
                            "attemptId": "tmdEN",
                            "answerFixed": true
                        },
                        {
                            "answer": "RED",
                            "checkType": "AUTO_CHECK",
                            "createdAt": "2024-04-17 20:49:48",
                            "id": "b66a7836-f5fb-4851-8c54-12370f21f9be",
                            "attemptId": "tmdEN",
                            "autoCheckInfo": {
                                "executionDotGraph": "digraph G {\\ngraph [splines=curved, nodesep=0.8]\\nnode [fontname = \\"helvetica\\"];\\nPENDING[shape=diamond, id=\\"PENDING\\",color=\\"#3598DC\\",fontcolor=\\"#3598DC\\",style=\\"rounded,filled\\",fillcolor=\\"#CCE5F6\\",fontsize=14,penwidth=1,label=<All docs<br/>submitted<br/>(Auto generated for POA_non-resident)>]\\n POA_CHECK[shape=rectangle, id=\\"POA_CHECK\\", color=\\"#19BC9B\\", fontcolor=\\"#19BC9B\\", style=\\"rounded,filled\\", fillcolor=\\"#C5EEE6\\", fontsize=14, penwidth=1, label=<Proof of address>]\\n PENDING -> POA_CHECK [color=\\"#19BC9B\\"]\\n CROSS_CHECK[shape=rectangle, id=\\"CROSS_CHECK\\", color=\\"#EF5656\\", fontcolor=\\"#EF5656\\", style=\\"rounded,filled\\", fillcolor=\\"#FBD5D5\\", fontsize=14, penwidth=1, label=<Cross-check<br/><FONT POINT-SIZE=\\"6\\">[Crosscheck inconsistency]</FONT><br/><FONT POINT-SIZE=\\"6\\">crossCheck</FONT>>]\\n POA_CHECK -> CROSS_CHECK [color=\\"#19BC9B\\"]\\n}",
                                "graphName": "Auto generated for POA_non-resident",
                                "notFullProcessedImages": [
                                    1657955595
                                ]
                            }
                        },
                        {
                            "answer": "RED",
                            "checkType": "SIMILAR_SEARCH",
                            "createdAt": "2024-04-17 20:50:31",
                            "id": "b0300032-90c3-46c0-8cf0-e4abd3398758",
                            "attemptId": "tmdEN",
                            "similarSearchInfo": null
                        },
                        {
                            "answer": "RED",
                            "checkType": "SIMILAR_SEARCH",
                            "createdAt": "2024-04-17 20:50:32",
                            "id": "53a24be8-0fc2-43ad-98ef-0f70f98e5694",
                            "attemptId": "tmdEN",
                            "similarSearchInfo": null
                        },
                        {
                            "answer": "RED",
                            "checkType": "AUTO_CHECK",
                            "createdAt": "2024-04-17 20:50:32",
                            "id": "8ba8f8f6-2717-40c6-a4cd-def106957d30",
                            "attemptId": "tmdEN",
                            "autoCheckInfo": {
                                "executionDotGraph": "digraph G {\\ngraph [splines=curved, nodesep=0.8]\\nnode [fontname = \\"helvetica\\"];\\nPENDING[shape=diamond, id=\\"PENDING\\",color=\\"#3598DC\\",fontcolor=\\"#3598DC\\",style=\\"rounded,filled\\",fillcolor=\\"#CCE5F6\\",fontsize=14,penwidth=1,label=<All docs<br/>submitted<br/>(Auto generated for POA_non-resident)>]\\n POA_CHECK_POACheckalreadygreen[shape=rectangle, id=\\"POA_CHECK_POACheckalreadygreen\\", color=\\"#19BC9B\\", fontcolor=\\"#19BC9B\\", style=\\"rounded,filled\\", fillcolor=\\"#D4FDF5\\", fontsize=14, penwidth=1, label=<Proof of address<br/> (poa check already green)>]\\n PENDING -> POA_CHECK_POACheckalreadygreen [color=\\"#19BC9B\\"]\\n CROSS_CHECK_Crosscheckispassed[shape=rectangle, id=\\"CROSS_CHECK_Crosscheckispassed\\", color=\\"#19BC9B\\", fontcolor=\\"#19BC9B\\", style=\\"rounded,filled\\", fillcolor=\\"#D4FDF5\\", fontsize=14, penwidth=1, label=<Cross-check<br/> (cross check is passed)>]\\n POA_CHECK_POACheckalreadygreen -> CROSS_CHECK_Crosscheckispassed [color=\\"#19BC9B\\"]\\n FRAUD_DUPLICATES[shape=rectangle, id=\\"FRAUD_DUPLICATES\\", color=\\"#19BC9B\\", fontcolor=\\"#19BC9B\\", style=\\"rounded,filled\\", fillcolor=\\"#C5EEE6\\", fontsize=14, penwidth=1, label=<Suspected fraud duplicates>]\\n CROSS_CHECK_Crosscheckispassed -> FRAUD_DUPLICATES [color=\\"#19BC9B\\"]\\n SIMILAR_SEARCH[shape=rectangle, id=\\"SIMILAR_SEARCH\\", color=\\"#EF5656\\", fontcolor=\\"#EF5656\\", style=\\"rounded,filled\\", fillcolor=\\"#FBD5D5\\", fontsize=14, penwidth=1, label=<Similar applicants search<br/><FONT POINT-SIZE=\\"6\\">[Duplicate hit detected]</FONT><br/><FONT POINT-SIZE=\\"6\\">duplicates</FONT>>]\\n CROSS_CHECK_Crosscheckispassed -> SIMILAR_SEARCH [color=\\"#19BC9B\\"]\\n FRAUD_DUPLICATES -> SIMILAR_SEARCH [color=\\"#19BC9B\\"]\\n}",
                                "graphName": "Auto generated for POA_non-resident",
                                "notFullProcessedImages": null
                            }
                        }
                    ]
                }""", "5cb56e8e0a975a35f333cb84", "5cb56e8e0a975a35f333cb83");
        return webhookParser.parse(bodyString, InspectionInfoResponse.class);
    }

    private Date fromLocalDate(LocalDate date) {
        return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    @Test
    void testProcessInvalidSignatureError() {
        IdentityCheckWebhookException exception = assertThrows(IdentityCheckWebhookException.class,
                () -> webhookProcessor.process("somepayload", new BaseWebhookBody(), "somedigest", ALG_KEY));
        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), exception.getResponseStatus());
        assertEquals("signature verification failure", exception.getResponseEntity().toString());
    }

    @Test
    void testPostApplicantCreatedWebhook() throws Exception {
        String rawPayload = """
                {
                  "applicantId": "5c9e177b0a975a6eeccf5960",
                  "inspectionId": "5c9e177b0a975a6eeccf5961",
                  "correlationId": "req-63f92830-4d68-4eee-98d5-875d53a12258",
                  "levelName": "basic-kyc-level",
                  "externalUserId": "12672",
                  "type": "applicantCreated",
                  "sandboxMode": "false",
                  "reviewStatus": "init",
                  "createdAtMs": "2020-02-21 13:23:19.002",
                  "clientId": "coolClientId"
                }""";

        webhookProcessor.process(rawPayload,
                webhookParser.parse(rawPayload, BaseWebhookBody.class),
                "e520b9e95ab07ffb96e9927d78cdeab04f6938c8221dce80607c4243fe8d2546",
                ALG_KEY
        );

        // since this is not applicant reviewed, no API should be called to get applicant data
        verify(apiService, never()).getApplicantByExternalId(any());
        verify(apiService, never()).getInspectionInfo(any());
    }

    @Test
    void testPostApplicantReviewedWebhookGreen() throws Exception {
        String rawPayload = """
                {
                  "applicantId": "5cb56e8e0a975a35f333cb83",
                  "inspectionId": "5cb56e8e0a975a35f333cb84",
                  "correlationId": "req-a260b669-4f14-4bb5-a4c5-ac0218acb9a4",
                  "externalUserId": "123",
                  "levelName": "basic-kyc-level",
                  "type": "applicantReviewed",
                  "reviewResult": {
                    "reviewAnswer": "GREEN"
                  },
                  "reviewStatus": "completed",
                  "createdAtMs": "2020-02-21 13:23:19.321"
                }""";


        IIdentity identity = mock(IIdentity.class);
        when(identity.getState()).thenReturn(IIdentityBase.STATE_TO_BE_VERIFIED);
        when(identity.getPublicId()).thenReturn("123");

        when(apiService.getApplicantByExternalId("123")).thenReturn(getApplicantInfoResponseDrivers());
        when(apiService.getInspectionInfo("5cb56e8e0a975a35f333cb84")).thenReturn(getInpsectionInfoResponse());
        when(ctx.findIdentityByIdentityId("123")).thenReturn(identity);

        webhookProcessor.process(rawPayload,
                webhookParser.parse(rawPayload, BaseWebhookBody.class),
                "5a2e30f8fc250abc21d32223a9a3e6bd884d30383351b392e7a2aa27257edce5",
                ALG_KEY
        );
        verify(apiService, times(1)).getApplicantByExternalId(any());
        verify(apiService, times(1)).getInspectionInfo(any());
        verify(ctx, never()).updateIdentity(
                anyString(), anyString(), anyInt(), anyInt(), any(), any(),
                any(), any(), anyString(), anyList(), anyList(),
                anyList(), anyList(), anyList(), anyList(), anyList(), anyList(),
                anyList(), anyList(), anyString());


        ArgumentCaptor<ApplicantCheckResult> checkResultArgumentCaptor = ArgumentCaptor.forClass(ApplicantCheckResult.class);
        verify(ctx, times(1)).processIdentityVerificationResult(any(), checkResultArgumentCaptor.capture());

        ApplicantCheckResult applicantCheckResult = checkResultArgumentCaptor.getValue();
        assertEquals(CheckResult.CLEAR, applicantCheckResult.getResult());

        // personal
        assertEquals("CHRISTIAN", applicantCheckResult.getFirstName());
        assertEquals("SMITH", applicantCheckResult.getLastName());
        assertEquals(fromLocalDate(LocalDate.of(1989, 7, 16)), applicantCheckResult.getBirthDate());

        // document
        assertEquals(DocumentType.passport, applicantCheckResult.getDocumentType());
        assertEquals("1234567890", applicantCheckResult.getDocumentNumber());
        assertEquals(fromLocalDate(LocalDate.of(2030, 1, 1)), applicantCheckResult.getExpirationDate());
        assertEquals("CAN", applicantCheckResult.getCountry());

        // address
        assertEquals("22 MOUNT STREET", applicantCheckResult.getStreetAddress());
        assertEquals("TORONTO", applicantCheckResult.getCity());
        assertEquals("M5A1L2", applicantCheckResult.getZip());
        assertEquals("ON", applicantCheckResult.getState());
        assertEquals("CAN", applicantCheckResult.getCountry());
        assertEquals("22 MOUNT STREET, TORONTO, ON, CANADA, M5A1L2", applicantCheckResult.getRawAddress());

        // document download is triggered in background - verify it was invoked
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() ->
            verify(documentDownloader, atLeastOnce()).downloadAndStoreDocuments(eq("123"), eq("5cb56e8e0a975a35f333cb84"), anyList(), eq(ctx)));
    }

    @Test
    void testPostApplicantReviewedWebhookDocumentDownloadSkippedWhenIdentityNull() throws Exception {
        String rawPayload = """
            {
              "applicantId": "5cb56e8e0a975a35f333cb83",
              "inspectionId": "5cb56e8e0a975a35f333cb84",
              "correlationId": "req-a260b669-4f14-4bb5-a4c5-ac0218acb9a4",
              "externalUserId": "123",
              "levelName": "basic-kyc-level",
              "type": "applicantReviewed",
              "reviewResult": {
                "reviewAnswer": "GREEN"
              },
              "reviewStatus": "completed",
              "createdAtMs": "2020-02-21 13:23:19.321"
            }""";

        when(apiService.getApplicantByExternalId("123")).thenReturn(getApplicantInfoResponseDrivers());
        when(apiService.getInspectionInfo("5cb56e8e0a975a35f333cb84")).thenReturn(getInpsectionInfoResponse());
        when(ctx.findIdentityByIdentityId("123")).thenReturn(null);

        webhookProcessor.process(rawPayload,
            webhookParser.parse(rawPayload, BaseWebhookBody.class),
            "5a2e30f8fc250abc21d32223a9a3e6bd884d30383351b392e7a2aa27257edce5",
            ALG_KEY
        );

        verify(ctx, times(1)).processIdentityVerificationResult(any(), any());
        verify(documentDownloader, never()).downloadAndStoreDocuments(anyString(), anyString(), anyList(), any());
    }

    @Test
    void testPostApplicantReviewedWebhookDocumentDownloadSkippedWhenImagesEmpty() throws Exception {
        String rawPayload = """
            {
              "applicantId": "5cb56e8e0a975a35f333cb83",
              "inspectionId": "5cb56e8e0a975a35f333cb84",
              "correlationId": "req-a260b669-4f14-4bb5-a4c5-ac0218acb9a4",
              "externalUserId": "123",
              "levelName": "basic-kyc-level",
              "type": "applicantReviewed",
              "reviewResult": {
                "reviewAnswer": "GREEN"
              },
              "reviewStatus": "completed",
              "createdAtMs": "2020-02-21 13:23:19.321"
            }""";

        IIdentity identity = mock(IIdentity.class);
        when(identity.getState()).thenReturn(IIdentityBase.STATE_TO_BE_VERIFIED);

        InspectionInfoResponse inspectionWithEmptyImages = webhookParser.parse("""
            {
                "id": "5cb56e8e0a975a35f333cb84",
                "inspectionDate": "2024-04-17 20:40:13",
                "applicantId": "5cb56e8e0a975a35f333cb83",
                "images": []
            }""", InspectionInfoResponse.class);

        when(apiService.getApplicantByExternalId("123")).thenReturn(getApplicantInfoResponseDrivers());
        when(apiService.getInspectionInfo("5cb56e8e0a975a35f333cb84")).thenReturn(inspectionWithEmptyImages);
        when(ctx.findIdentityByIdentityId("123")).thenReturn(identity);

        webhookProcessor.process(rawPayload,
            webhookParser.parse(rawPayload, BaseWebhookBody.class),
            "5a2e30f8fc250abc21d32223a9a3e6bd884d30383351b392e7a2aa27257edce5",
            ALG_KEY
        );

        verify(ctx, times(1)).processIdentityVerificationResult(any(), any());
        verify(documentDownloader, never()).downloadAndStoreDocuments(anyString(), anyString(), anyList(), any());
    }

    @Test
    void testPostApplicantReviewedWebhookRed() throws Exception {
        String rawPayload = """
                {
                  "applicantId": "5cb56e8e0a975a35f333cb83",
                  "inspectionId": "5cb56e8e0a975a35f333cb84",
                  "correlationId": "req-a260b669-4f14-4bb5-a4c5-ac0218acb9a4",
                  "externalUserId": "123",
                  "levelName": "basic-kyc-level",
                  "type": "applicantReviewed",
                  "reviewResult": {
                    "reviewAnswer": "RED",
                    "reviewRejectType": "FINAL"
                  },
                  "reviewStatus": "completed",
                  "createdAtMs": "2020-02-21 13:23:19.321"
                }""";

        IIdentity identity = mock(IIdentity.class);
        when(identity.getState()).thenReturn(IIdentityBase.STATE_REGISTERED);
        when(identity.getPublicId()).thenReturn("123");
        when(identity.getCreated()).thenReturn(new Date());
        when(identity.getRegistered()).thenReturn(new Date());
        when(identity.getVipBuyDiscount()).thenReturn(BigDecimal.ZERO);
        when(identity.getVipSellDiscount()).thenReturn(BigDecimal.ZERO);
        when(identity.getConfigurationCashCurrency()).thenReturn("CAD");

        when(apiService.getApplicantByExternalId("123")).thenReturn(getApplicantInfoResponseDrivers());
        when(apiService.getInspectionInfo("5cb56e8e0a975a35f333cb84")).thenReturn(getInpsectionInfoResponse());
        when(ctx.findIdentityByIdentityId("123")).thenReturn(identity);

        webhookProcessor.process(rawPayload,
                webhookParser.parse(rawPayload, BaseWebhookBody.class),
                "52ce0a5965cac714ecb005959c268f89845bee75c0b0b6c43c6e78aa102fd038",
                ALG_KEY
        );
        verify(apiService, times(1)).getApplicantByExternalId(any());
        verify(apiService, times(1)).getInspectionInfo(any());
        verify(ctx, times(1)).updateIdentity(
                anyString(), nullable(String.class), anyInt(), anyInt(), any(), any(),
                any(), any(), anyString(), nullable(List.class), nullable(List.class),
                nullable(List.class), nullable(List.class), nullable(List.class), nullable(List.class), nullable(List.class), nullable(List.class),
                nullable(List.class), nullable(List.class), anyString());

        ArgumentCaptor<ApplicantCheckResult> checkResultArgumentCaptor = ArgumentCaptor.forClass(ApplicantCheckResult.class);
        verify(ctx, times(1)).processIdentityVerificationResult(any(), checkResultArgumentCaptor.capture());

        ApplicantCheckResult applicantCheckResult = checkResultArgumentCaptor.getValue();
        assertEquals(CheckResult.REJECTED, applicantCheckResult.getResult());

        // personal
        assertEquals("CHRISTIAN", applicantCheckResult.getFirstName());
        assertEquals("SMITH", applicantCheckResult.getLastName());
        assertEquals(fromLocalDate(LocalDate.of(1989, 7, 16)), applicantCheckResult.getBirthDate());

        // document
        assertEquals(DocumentType.passport, applicantCheckResult.getDocumentType());
        assertEquals("1234567890", applicantCheckResult.getDocumentNumber());
        assertEquals(fromLocalDate(LocalDate.of(2030, 1, 1)), applicantCheckResult.getExpirationDate());
        assertEquals("CAN", applicantCheckResult.getCountry());

        // address
        assertEquals("22 MOUNT STREET", applicantCheckResult.getStreetAddress());
        assertEquals("TORONTO", applicantCheckResult.getCity());
        assertEquals("M5A1L2", applicantCheckResult.getZip());
        assertEquals("ON", applicantCheckResult.getState());
        assertEquals("CAN", applicantCheckResult.getCountry());
        assertEquals("22 MOUNT STREET, TORONTO, ON, CANADA, M5A1L2", applicantCheckResult.getRawAddress());
    }

    @Test
    void testPostApplicantReviewedWebhookHttpStatusIOException() throws Exception {
        String rawPayload = """
                {
                  "applicantId": "5cb56e8e0a975a35f333cb83",
                  "inspectionId": "5cb56e8e0a975a35f333cb84",
                  "correlationId": "req-a260b669-4f14-4bb5-a4c5-ac0218acb9a4",
                  "externalUserId": "123",
                  "levelName": "basic-kyc-level",
                  "type": "applicantReviewed",
                  "reviewResult": {
                    "reviewAnswer": "RED",
                    "reviewRejectType": "FINAL"
                  },
                  "reviewStatus": "completed",
                  "createdAtMs": "2020-02-21 13:23:19.321"
                }""";


        when(apiService.getApplicantByExternalId("123"))
                .thenThrow(new HttpStatusIOException("Some Http Error", new InvocationResult("Some Body", 400)));

        BaseWebhookBody baseWebhookBody = webhookParser.parse(rawPayload, BaseWebhookBody.class);
        IdentityCheckWebhookException exception = assertThrows(IdentityCheckWebhookException.class, () -> webhookProcessor.process(rawPayload,
                baseWebhookBody,
                "52ce0a5965cac714ecb005959c268f89845bee75c0b0b6c43c6e78aa102fd038",
                ALG_KEY
        ));
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponseStatus());
        assertEquals("Error getting info from SumSub.", exception.getMessage());
    }

    @Test
    void testPostApplicantReviewedWebhookUnknownException() throws Exception {
        String rawPayload = """
                {
                  "applicantId": "5cb56e8e0a975a35f333cb83",
                  "inspectionId": "5cb56e8e0a975a35f333cb84",
                  "correlationId": "req-a260b669-4f14-4bb5-a4c5-ac0218acb9a4",
                  "externalUserId": "123",
                  "levelName": "basic-kyc-level",
                  "type": "applicantReviewed",
                  "reviewResult": {
                    "reviewAnswer": "RED",
                    "reviewRejectType": "FINAL"
                  },
                  "reviewStatus": "completed",
                  "createdAtMs": "2020-02-21 13:23:19.321"
                }""";


        when(apiService.getApplicantByExternalId("123"))
                .thenThrow(new NullPointerException("some NPE"));

        BaseWebhookBody baseWebhookBody = webhookParser.parse(rawPayload, BaseWebhookBody.class);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> webhookProcessor.process(rawPayload,
                baseWebhookBody,
                "52ce0a5965cac714ecb005959c268f89845bee75c0b0b6c43c6e78aa102fd038",
                ALG_KEY
        ));
        assertInstanceOf(RuntimeException.class, exception);

    }

    @Test
    void testPostApplicantLevelChangedWebhook() throws Exception {
        String rawPayload = """
                {
                  "applicantId": "5f194e74040c3f316bda271c",
                  "inspectionId": "5f194e74040c3f316bda271d",
                  "applicantType": "individual",
                  "correlationId": "req-d34c974c-5935-41b8-a0a9-cedd2407eadd",
                  "levelName": "basic-kyc-level",
                  "externalUserId": "12672",
                  "type": "applicantLevelChanged",
                  "sandboxMode": "false",
                  "reviewStatus": "init",
                  "createdAtMs": "2020-07-23 11:19:33.002",
                  "clientId": "coolClientId"
                }""";


        IIdentity identity = mock(IIdentity.class);
        when(identity.getCreatedByTerminalSerialNumber()).thenReturn("BT000000");

        Instant now = Instant.now();

        IIdentityPiece cellPhone1 = mock(IIdentityPiece.class);
        when(cellPhone1.getCreated()).thenReturn(new Date(now.toEpochMilli()));
        when(cellPhone1.getPieceType()).thenReturn(IIdentityPiece.TYPE_CELLPHONE);

        IIdentityPiece cellPhone2 = mock(IIdentityPiece.class);
        when(cellPhone2.getPhoneNumber()).thenReturn("+9123456789");
        when(cellPhone2.getCreated()).thenReturn(new Date(now.plusSeconds(100L).toEpochMilli()));
        when(cellPhone2.getPieceType()).thenReturn(IIdentityPiece.TYPE_CELLPHONE);

        IIdentityPiece personalInfo = mock(IIdentityPiece.class);
        when(personalInfo.getPieceType()).thenReturn(IIdentityPiece.TYPE_PERSONAL_INFORMATION);

        when(identity.getIdentityPieces()).thenReturn(List.of(cellPhone1, cellPhone2, personalInfo));

        CreateIdentityVerificationSessionResponse r2 = new CreateIdentityVerificationSessionResponse("https://hi.org");

        when(apiService.createSession(eq("12672"), isNull())).thenReturn(r2);
        when(ctx.findIdentityByIdentityId("12672")).thenReturn(identity);

        webhookProcessor.process(rawPayload,
                webhookParser.parse(rawPayload, BaseWebhookBody.class),
                "75d41570f78bc67a31bf650b24e33b5b23b31eedc447d1645111e12ac1112201",
                ALG_KEY
        );
        verify(ctx, times(1)).sendSMSAsync("BT000000", "+9123456789",
                "Please use the following link to continue your verification process. https://hi.org");
    }


    @Test
    void testPostApplicantLevelChangedWebhook_sameApplicantLevel() throws Exception {
        String rawPayload = """
                {
                  "applicantId": "5f194e74040c3f316bda271c",
                  "inspectionId": "5f194e74040c3f316bda271d",
                  "applicantType": "individual",
                  "correlationId": "req-d34c974c-5935-41b8-a0a9-cedd2407eadd",
                  "levelName": "basic-kyc-level",
                  "externalUserId": "12672",
                  "type": "applicantLevelChanged",
                  "sandboxMode": "false",
                  "reviewStatus": "init",
                  "createdAtMs": "2020-07-23 11:19:33.002",
                  "clientId": "coolClientId"
                }""";

        when(apiService.getStartingLevelName()).thenReturn("basic-kyc-level");

        webhookProcessor.process(rawPayload,
                webhookParser.parse(rawPayload, BaseWebhookBody.class),
                "75d41570f78bc67a31bf650b24e33b5b23b31eedc447d1645111e12ac1112201",
                ALG_KEY
        );
        verifyNoInteractions(ctx);
    }

    @Test
    void testPostApplicantLevelChangedWebhookHttpStatusError() throws Exception {
        String rawPayload = """
                {
                  "applicantId": "5f194e74040c3f316bda271c",
                  "inspectionId": "5f194e74040c3f316bda271d",
                  "applicantType": "individual",
                  "correlationId": "req-d34c974c-5935-41b8-a0a9-cedd2407eadd",
                  "levelName": "basic-kyc-level",
                  "externalUserId": "12672",
                  "type": "applicantLevelChanged",
                  "sandboxMode": "false",
                  "reviewStatus": "init",
                  "createdAtMs": "2020-07-23 11:19:33.002",
                  "clientId": "coolClientId"
                }""";


        IIdentity identity = mock(IIdentity.class);

        Instant now = Instant.now();

        IIdentityPiece cellPhone1 = mock(IIdentityPiece.class);
        when(cellPhone1.getCreated()).thenReturn(new Date(now.toEpochMilli()));
        when(cellPhone1.getPieceType()).thenReturn(IIdentityPiece.TYPE_CELLPHONE);

        IIdentityPiece cellPhone2 = mock(IIdentityPiece.class);
        when(cellPhone2.getCreated()).thenReturn(new Date(now.plusSeconds(100L).toEpochMilli()));
        when(cellPhone2.getPieceType()).thenReturn(IIdentityPiece.TYPE_CELLPHONE);

        IIdentityPiece personalInfo = mock(IIdentityPiece.class);
        when(personalInfo.getPieceType()).thenReturn(IIdentityPiece.TYPE_PERSONAL_INFORMATION);

        when(identity.getIdentityPieces()).thenReturn(List.of(cellPhone1, cellPhone2, personalInfo));

        when(ctx.findIdentityByIdentityId("12672")).thenReturn(identity);
        when(apiService.createSession(eq("12672"), isNull()))
                .thenThrow(new HttpStatusIOException("Some Http Error", new InvocationResult("Some Body", 400)));

        BaseWebhookBody baseWebhookBody = webhookParser.parse(rawPayload, BaseWebhookBody.class);
        IdentityCheckWebhookException exception = assertThrows(IdentityCheckWebhookException.class, () -> webhookProcessor.process(rawPayload,
                baseWebhookBody,
                "75d41570f78bc67a31bf650b24e33b5b23b31eedc447d1645111e12ac1112201",
                ALG_KEY
        ));
        verify(ctx, never()).sendSMSAsync(anyString(), anyString(), anyString());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), exception.getResponseStatus());
        assertEquals("Error creating SumSub session.", exception.getMessage());
    }

    @Test
    void testPostApplicantLevelChangedWebhookOtherException() throws IdentityCheckWebhookException {
        String rawPayload = """
                {
                  "applicantId": "5f194e74040c3f316bda271c",
                  "inspectionId": "5f194e74040c3f316bda271d",
                  "applicantType": "individual",
                  "correlationId": "req-d34c974c-5935-41b8-a0a9-cedd2407eadd",
                  "levelName": "basic-kyc-level",
                  "externalUserId": "12672",
                  "type": "applicantLevelChanged",
                  "sandboxMode": "false",
                  "reviewStatus": "init",
                  "createdAtMs": "2020-07-23 11:19:33.002",
                  "clientId": "coolClientId"
                }""";

        BaseWebhookBody parsedWebhookBody = webhookParser.parse(rawPayload, BaseWebhookBody.class);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> webhookProcessor.process(rawPayload,
                parsedWebhookBody,
                "75d41570f78bc67a31bf650b24e33b5b23b31eedc447d1645111e12ac1112201",
                ALG_KEY
        ));
        assertInstanceOf(RuntimeException.class, exception);
    }
}
