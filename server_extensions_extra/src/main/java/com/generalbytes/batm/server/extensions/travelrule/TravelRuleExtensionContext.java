package com.generalbytes.batm.server.extensions.travelrule;


import com.generalbytes.batm.server.extensions.IExtensionContext;
import lombok.AllArgsConstructor;

/**
 * A facade that provides a simplified interface for travel rule related operations from {@code IExtensionContext}.
 */
@AllArgsConstructor
public class TravelRuleExtensionContext {

    private final IExtensionContext context;

    public ITravelRuleTransferData findTravelRuleTransferByAddress(String address) {
        return context.findTravelRuleTransferByAddress(address);
    }
}
