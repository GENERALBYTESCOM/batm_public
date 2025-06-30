package com.generalbytes.batm.server.extensions.extra.identityverification.sumsub;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class SumSubInstanceModuleTest {

    @Test
    void testModuleGetCtx() {
        SumSubInstanceModule module = SumSubInstanceModule.getInstance();

        IExtensionContext extensionContext = mock(IExtensionContext.class);

        module.addService(IExtensionContext.class, extensionContext);

        assertEquals(extensionContext, module.getCtx());
    }

    @Test
    void testGetWebhookParser() {
        SumSubInstanceModule module = SumSubInstanceModule.getInstance();

        SumSubWebhookParser parser = mock(SumSubWebhookParser.class);

        module.addService(SumSubWebhookParser.class, parser);

        assertEquals(parser, module.getSubWebhookParser());
    }

    @Test
    void testAddServicesInvalidInstance() {
        SumSubInstanceModule module = SumSubInstanceModule.getInstance();
        module.addService(SumSubIdentityVerificationProvider.class, mock(SumSubWebhookParser.class));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> module.getService(SumSubIdentityVerificationProvider.class));

        assertEquals(
                "Service com.generalbytes.batm.server.extensions.extra.identityverification.sumsub.SumSubIdentityVerificationProvider not initialized yet",
                exception.getMessage()
        );
    }
}