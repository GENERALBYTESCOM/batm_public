package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.aml.verification.CreateApplicantResponse;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.BaseWebhookBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SumSubWebhookRestServiceTest {
    private static final String APPLICANT_ID = "applicantId";

    @Mock
    private SumSubWebhookParser webhookParser;
    @Mock
    private IExtensionContext extensionContext;
    @InjectMocks
    private SumSubWebhookRestService service;

    @BeforeEach
    void setUp() {
        SumSubInstanceModule module = SumSubInstanceModule.getInstance();
        module.addService(SumSubWebhookParser.class, webhookParser);
        module.addService(IExtensionContext.class, extensionContext);
    }

    @Test
    void testGetPrefixPath() {
        assertEquals(SumSubWebhookRestService.PREFIX_PATH, service.getPrefixPath());
    }

    @Test
    void testGetImplementation() {
        assertEquals(SumSubWebhookRestService.class, service.getImplementation());
    }

    @Test
    void testSumsubWebhookTest() {
        String responseContent = service.sumsubWebhookTest();
        assertEquals(SumSubWebhookRestService.DEFAULT_TEST_MESSAGE, responseContent);
    }

    @Test
    void testSumsubWebhook() throws IdentityCheckWebhookException {
        BaseWebhookBody baseWebhookBody = createBaseWebhookBody();
        when(webhookParser.parse("rawPayload", BaseWebhookBody.class)).thenReturn(baseWebhookBody);
        SumSubIdentityVerificationProvider provider = mock(SumSubIdentityVerificationProvider.class);
        when(extensionContext.findIdentityVerificationProviderByApplicantId(APPLICANT_ID)).thenReturn(provider);


        Response response = service.sumsubWebhook("rawPayload", "digest", "alg");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        verify(provider).processWebhook("rawPayload", baseWebhookBody, "digest", "alg" );
    }

    @Test
    void testSumsubWebhook_processWebhookFail() throws IdentityCheckWebhookException {
        when(webhookParser.parse("rawPayload", BaseWebhookBody.class))
                .thenThrow(new IdentityCheckWebhookException(Response.Status.BAD_REQUEST.getStatusCode(), "Failed to parse request data", "rawPayload"));

        Response response = service.sumsubWebhook("rawPayload", "digest", "alg");
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals("Failed to parse request data", response.getEntity().toString());

        verifyNoInteractions(extensionContext);
    }

    @Test
    void testSumsubWebhook_noProvider() throws IdentityCheckWebhookException {
        BaseWebhookBody baseWebhookBody = createBaseWebhookBody();
        when(webhookParser.parse("rawPayload", BaseWebhookBody.class)).thenReturn(baseWebhookBody);
        when(extensionContext.findIdentityVerificationProviderByApplicantId(APPLICANT_ID)).thenReturn(null);

        Response response = service.sumsubWebhook("rawPayload", "digest", "alg");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testSumsubWebhook_invalidProvider() throws IdentityCheckWebhookException {
        BaseWebhookBody baseWebhookBody = createBaseWebhookBody();
        when(webhookParser.parse("rawPayload", BaseWebhookBody.class)).thenReturn(baseWebhookBody);
        DifferentProvider provider = mock(DifferentProvider.class);
        when(extensionContext.findIdentityVerificationProviderByApplicantId(APPLICANT_ID)).thenReturn(provider);

        Response response = service.sumsubWebhook("rawPayload", "digest", "alg");
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void testSumsubWebhook_unexpectedError() throws IdentityCheckWebhookException {
        BaseWebhookBody baseWebhookBody = createBaseWebhookBody();
        when(webhookParser.parse("rawPayload", BaseWebhookBody.class)).thenReturn(baseWebhookBody);
        SumSubIdentityVerificationProvider provider = mock(SumSubIdentityVerificationProvider.class);
        doThrow(new RuntimeException("test-unexpected-exception"))
                .when(provider).processWebhook("rawPayload", baseWebhookBody, "digest", "alg");

        when(extensionContext.findIdentityVerificationProviderByApplicantId(APPLICANT_ID)).thenReturn(provider);


        Response response = service.sumsubWebhook("rawPayload", "digest", "alg");
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertEquals("Failed to process webhook", response.getEntity().toString());
    }

    private BaseWebhookBody createBaseWebhookBody() {
        BaseWebhookBody body = mock(BaseWebhookBody.class);
        when(body.getApplicantId()).thenReturn(APPLICANT_ID);
        return body;
    }

    private static class DifferentProvider implements IIdentityVerificationProvider {

        @Override
        public CreateApplicantResponse createApplicant(String s, String s1) {
            return null;
        }
    }
}