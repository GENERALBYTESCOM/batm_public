package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.ISumSubApi;
import com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.api.vo.CreateIdentityApplicantRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SumSubApiServiceTest {
    private static final String LEVEL_NAME = "levelName";
    private static final int LINK_EXPIRY_IN_SECONDS = 1000000;
    private static final String IDENTITY_PUBLIC_ID = "identityPublicId";
    private static final String CUSTOMER_LANGUAGE = "en";

    @Mock
    private ISumSubApi api;
    private SumSubApiService service;

    @BeforeEach
    void setUp() {
        service = new SumSubApiService(api, LEVEL_NAME, LINK_EXPIRY_IN_SECONDS);
    }

    @Test
    void testCreateSession() throws IOException {
        service.createSession(IDENTITY_PUBLIC_ID, CUSTOMER_LANGUAGE);
        verify(api).createSession(LEVEL_NAME, LINK_EXPIRY_IN_SECONDS, IDENTITY_PUBLIC_ID, CUSTOMER_LANGUAGE);
    }

    @Test
    void testCreateApplicant() throws IOException {
        service.createApplicant(IDENTITY_PUBLIC_ID);

        ArgumentCaptor<CreateIdentityApplicantRequest> captor = ArgumentCaptor.forClass(CreateIdentityApplicantRequest.class);
        verify(api).createApplicant(captor.capture(), eq(LEVEL_NAME));

        CreateIdentityApplicantRequest request = captor.getValue();
        assertEquals("individual", request.getType());
        assertEquals(IDENTITY_PUBLIC_ID, request.getExternalUserId());
    }

    @Test
    void testGetApplicantByExternalId() throws IOException {
        service.getApplicantByExternalId(IDENTITY_PUBLIC_ID);

        verify(api).getApplicantByExternalId(IDENTITY_PUBLIC_ID);
    }

    @Test
    void testGetInspectionInfo() throws IOException {
        service.getInspectionInfo(IDENTITY_PUBLIC_ID);

        verify(api).getInspectionInfo(IDENTITY_PUBLIC_ID);
    }

    @Test
    void testGetStartingLevelName() {
        String startingLevelName = service.getStartingLevelName();
        assertEquals(LEVEL_NAME, startingLevelName);
    }
}