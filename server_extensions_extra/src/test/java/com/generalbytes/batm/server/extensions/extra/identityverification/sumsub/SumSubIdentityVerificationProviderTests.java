package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.aml.verification.CreateApplicantResponse;
import com.generalbytes.batm.server.extensions.aml.verification.IdentityCheckWebhookException;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantIdResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.ApplicantInfoResponse;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.BaseWebhookBody;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.CreateIdentityVerificationSessionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.InvocationResult;

import jakarta.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SumSubIdentityVerificationProviderTests {

    @Mock
    private SumSubApiService apiService;
    @Mock
    private SumSubWebhookProcessor webhookProcessor;
    @Mock
    private IExtensionContext ctx;

    private SumSubIdentityVerificationProvider provider;

    @BeforeEach
    void setUp() {
        ctx = mock(IExtensionContext.class);
        new SumSubExtension().init(ctx);
        provider = new SumSubIdentityVerificationProvider(apiService, webhookProcessor);
    }

    @Test
    void testCreateApplicantNewApplicant() throws Exception {
        ApplicantIdResponse r = new ApplicantIdResponse("applicantId");
        CreateIdentityVerificationSessionResponse r2 = new CreateIdentityVerificationSessionResponse("https://hi.org");

        when(apiService.createApplicant("IDENTITY1")).thenReturn(r);
        when(apiService.createSession("IDENTITY1", "en")).thenReturn(r2);

        CreateApplicantResponse response = provider.createApplicant("en", "IDENTITY1");
        assertEquals("applicantId", response.getApplicantId());
        assertEquals("https://hi.org", response.getVerificationWebUrl());
        verify(apiService, times(0)).getApplicantByExternalId(any());
    }

    @Test
    void testCreateApplicantApplicantAlreadyExists() throws Exception {
        CreateIdentityVerificationSessionResponse r2 = new CreateIdentityVerificationSessionResponse("https://hi.org");
        ApplicantInfoResponse r3 = new ApplicantInfoResponse();
        r3.setId("applicantId");


        when(apiService.createApplicant("IDENTITY1")).thenThrow(new HttpStatusIOException("Some error message", new InvocationResult("Some body", 409)));
        when(apiService.getApplicantByExternalId("IDENTITY1")).thenReturn(r3);
        when(apiService.createSession("IDENTITY1", "en")).thenReturn(r2);

        CreateApplicantResponse response = provider.createApplicant("en", "IDENTITY1");
        assertEquals("applicantId", response.getApplicantId());
        assertEquals("https://hi.org", response.getVerificationWebUrl());
    }

    @Test
    void testCreateApplicantCreateApplicantHttpError() throws Exception {
        when(apiService.createApplicant("IDENTITY1")).thenThrow(new HttpStatusIOException("Some error message", new InvocationResult("Some body", 400)));
        assertNull(provider.createApplicant("en", "IDENTITY1"));
    }

    @Test
    void testCreateApplicantCreateApplicantUnknownError() throws Exception {
        when(apiService.createApplicant("IDENTITY1")).thenThrow(new RuntimeException("Some error message"));
        assertNull(provider.createApplicant("en", "IDENTITY1"));
    }

    @Test
    void testCreateApplicantApplicantAlreadyExistsHttpError() throws Exception {
        when(apiService.createApplicant("IDENTITY1")).thenThrow(new HttpStatusIOException("Some error message", new InvocationResult("Some body", 409)));
        when(apiService.getApplicantByExternalId("IDENTITY1")).thenThrow(new HttpStatusIOException("Some error message", new InvocationResult("Some body", 400)));
        assertNull(provider.createApplicant("en", "IDENTITY1"));
    }

    @Test
    void testCreateApplicantApplicantAlreadyExistsUnknownError() throws Exception {
        when(apiService.createApplicant("IDENTITY1")).thenThrow(new HttpStatusIOException("Some error message", new InvocationResult("Some body", 409)));
        when(apiService.getApplicantByExternalId("IDENTITY1")).thenThrow(new RuntimeException("Some error message"));
        assertNull(provider.createApplicant("en", "IDENTITY1"));
    }

    @Test
    void testCreateApplicantApplicantCreateSessionHttpError() throws Exception {
        ApplicantIdResponse r = new ApplicantIdResponse("applicantId");

        when(apiService.createApplicant("IDENTITY1")).thenReturn(r);
        when(apiService.createSession("IDENTITY1", "en")).thenThrow(new HttpStatusIOException("Some error message", new InvocationResult("Some body", 400)));
        assertNull(provider.createApplicant("en", "IDENTITY1"));
    }

    @Test
    void testCreateApplicantApplicantCreateSessionUnknownError() throws Exception {
        ApplicantIdResponse r = new ApplicantIdResponse("applicantId");

        when(apiService.createApplicant("IDENTITY1")).thenReturn(r);
        when(apiService.createSession("IDENTITY1", "en")).thenThrow(new RuntimeException("Some error message"));
        assertNull(provider.createApplicant("en", "IDENTITY1"));
    }

    @Test
    void testProcessWebhookError() throws Exception {
        doThrow(new IdentityCheckWebhookException(Response.Status.BAD_REQUEST.getStatusCode(), "Failed to parse request data", "some payload"))
                .when(webhookProcessor).process(anyString(), any(), anyString(), anyString());


        IdentityCheckWebhookException exception = assertThrows(IdentityCheckWebhookException.class,
                () -> provider.processWebhook("payload", new BaseWebhookBody(), "digest", "alg"));


        assertEquals("some payload", exception.getMessage());
        assertEquals("Failed to parse request data", exception.getResponseEntity());
    }
}
