package com.generalbytes.batm.server.extensions.travelrule.gtr.handler;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.gtr.api.GtrApiConstants;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrAddressVerifyWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrPiiVerifyWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrReceiveTxIdWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrTxVerifyWebhookPayload;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessage;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookMessageResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrWebhookPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GtrVerifyAddressWebhookHandlerTest {

    @Mock
    private TravelRuleExtensionContext ctx;
    @InjectMocks
    private GtrVerifyAddressWebhookHandler handler;

    private static Object[] provideInvalidPayloadsForAddressVerification() {
        return new Object[]{
            new GtrTxVerifyWebhookPayload(),
            new GtrPiiVerifyWebhookPayload(),
            new GtrReceiveTxIdWebhookPayload()
        };
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("provideInvalidPayloadsForAddressVerification")
    void testHandleAddressVerificationRequest_invalidPayload(GtrWebhookPayload payload) {
        GtrWebhookMessage message = new GtrWebhookMessage();
        message.setCallbackData(payload);

        GtrWebhookMessageResponse response = handler.handle(message);

        assertNotNull(response);
        assertEquals("Invalid callbackData for address verification.", response.getVerifyMessage());
        assertEquals(GtrApiConstants.VerifyStatus.CLIENT_BAD_PARAMETERS, response.getVerifyStatus());
    }

    @Test
    void testHandleAddressVerificationRequest_noTransferFound() {
        GtrWebhookMessage message = new GtrWebhookMessage();
        GtrAddressVerifyWebhookPayload payload = new GtrAddressVerifyWebhookPayload();
        payload.setAddress("address");
        message.setCallbackData(payload);

        when(ctx.findTravelRuleTransferByAddress("address")).thenReturn(null);

        GtrWebhookMessageResponse response = handler.handle(message);

        assertNotNull(response);
        assertEquals(GtrApiConstants.VerifyStatus.ADDRESS_NOT_FOUND, response.getVerifyStatus());
        assertEquals("address not found", response.getVerifyMessage());
    }

    @Test
    void testHandleAddressVerificationRequest_transferFound() {
        GtrWebhookMessage message = new GtrWebhookMessage();
        GtrAddressVerifyWebhookPayload payload = new GtrAddressVerifyWebhookPayload();
        payload.setAddress("address");
        message.setCallbackData(payload);

        when(ctx.findTravelRuleTransferByAddress("address")).thenReturn(mock(ITravelRuleTransferData.class));

        GtrWebhookMessageResponse response = handler.handle(message);

        assertNotNull(response);
        assertEquals(GtrApiConstants.VerifyStatus.SUCCESS, response.getVerifyStatus());
        assertEquals("success", response.getVerifyMessage());
    }

}