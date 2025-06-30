package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.aml.verification.IIdentityVerificationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;


class SumSubExtensionTests {

    private SumSubExtension extension;

    @BeforeEach
    void setUp() {
        extension = new SumSubExtension();
    }

    @Test
    void testSumSubExtensionName() {
        assertEquals("BATM SumSub verification provider extension", extension.getName());
    }

    @Test
    void testSumSubExtensionNonInitRestServices() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, extension::getRestServices);

        assertEquals("Extension not initialized yet", exception.getMessage());
    }

    @Test()
    void testSumSubExtensionInitRestServices() {
        IExtensionContext context = mock(IExtensionContext.class);
        extension.init(context);
        Set<IRestService> restServiceSet = extension.getRestServices();
        assertEquals(1, restServiceSet.size());
        Optional<IRestService> restServiceOptional = restServiceSet.stream().findFirst();
        assertTrue(restServiceOptional.isPresent());
        assertInstanceOf(SumSubWebhookRestService.class, restServiceOptional.get());
    }

    @Test()
    void testSumSubExtensionInitContext() {
        IExtensionContext context = mock(IExtensionContext.class);
        extension.init(context);
        IExtensionContext initializedContext = SumSubInstanceModule.getInstance().getCtx();
        assertEquals(context, initializedContext);
    }

    @Test()
    void testSumSubExtensionCreateIdentityVerificationProviderValid() {
        extension.init(mock(IExtensionContext.class));
        IIdentityVerificationProvider provider = extension.createIdentityVerificationProvider("gbsumsub:token:secret:webhooksecret:levelName:1000000", "somegbapikeystring");
        assertNotNull(provider);
        assertInstanceOf(SumSubIdentityVerificationProvider.class, provider);
    }

    @Test()
    void testSumSubExtensionCreateIdentityVerificationProviderValidWithInvalidTTL() {
        extension.init(mock(IExtensionContext.class));
        assertNotNull(extension.createIdentityVerificationProvider("gbsumsub:token:secret:webhooksecret:levelName:notaninteger", "somegbapikeystring"));
    }

    @Test
    void testSumSubExtensionCreateIdentityVerificationProviderMissingParameter() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> extension.createIdentityVerificationProvider("gbsumsub:token:secret:webhooksecret", "somegbapikeystring"));
        assertEquals("SumSub parameter 'level name' cannot be null", exception.getMessage());
    }

    @Test()
    void testSumSubExtensionCreateIdentityVerificationProviderOtherVerificationProvider() {
        assertNull(extension.createIdentityVerificationProvider("gbsumsub1234:token:secret:webhooksecret:levelName", "somegbapikeystring"));
    }

}
