package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferStatusUpdateEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateListener;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleProviderTransferStatus;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferStatus;
import lombok.EqualsAndHashCode;

/**
 * This listener is responsible for mapping transfer status updates received from Notabene and sending them to a designated
 * {@link ITravelRuleTransferUpdateListener} for further processing on server side.
 */
@EqualsAndHashCode
public class NotabeneTransferStatusUpdateListener implements NotabeneTransferUpdateListener {

    private final ITravelRuleTransferUpdateListener transferUpdateHandler;

    public NotabeneTransferStatusUpdateListener(ITravelRuleTransferUpdateListener transferUpdateHandler) {
        this.transferUpdateHandler = transferUpdateHandler;
    }

    @Override
    public void onTransferUpdate(NotabeneTransferInfo updatedTransferInfo) {
        ITravelRuleTransferStatusUpdateEvent event = mapToTransferStatusUpdateEvent(updatedTransferInfo);
        transferUpdateHandler.onTransferStatusUpdate(event);
    }

    private ITravelRuleTransferStatusUpdateEvent mapToTransferStatusUpdateEvent(NotabeneTransferInfo updatedTransferInfo) {
        TravelRuleProviderTransferStatus status = mapToTravelRuleTransferStatus(updatedTransferInfo.getStatus());
        return new ITravelRuleTransferStatusUpdateEvent() {
            @Override
            public String getTransferPublicId() {
                return updatedTransferInfo.getTransactionRef();
            }

            @Override
            public TravelRuleProviderTransferStatus getNewTransferStatus() {
                return status;
            }
        };
    }

    private TravelRuleProviderTransferStatus mapToTravelRuleTransferStatus(NotabeneTransferStatus notabeneStatus) {
        return switch (notabeneStatus) {
            case ACCEPTED, SAVED -> TravelRuleProviderTransferStatus.APPROVED;
            case REJECTED, DECLINED, NOT_READY, CANCELLED -> TravelRuleProviderTransferStatus.REJECTED;
            default -> TravelRuleProviderTransferStatus.IN_PROGRESS;
        };
    }

}