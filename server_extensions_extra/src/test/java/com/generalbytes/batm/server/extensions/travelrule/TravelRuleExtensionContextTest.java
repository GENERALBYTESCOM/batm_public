package com.generalbytes.batm.server.extensions.travelrule;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TravelRuleExtensionContextTest {
    @Test
    void testFindTravelRuleTransferByAddress() {
        IExtensionContext context = mock(IExtensionContext.class);
        TravelRuleExtensionContext travelRuleExtensionContext = new TravelRuleExtensionContext(context);
        travelRuleExtensionContext.findTravelRuleTransferByAddress("address");
        verify(context).findTravelRuleTransferByAddress("address");
    }
}