package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.notabene.dto.NotabeneTransferInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A publisher for propagating updates to Notabene transfers to registered listeners.
 *
 * <p>This class implements a publish-subscribe mechanism, where services interested in receiving
 * updates about Notabene transfers can register listeners. When an update occurs,
 * all registered listeners are notified.</p>
 *
 * @see NotabeneTransferUpdateListener
 */
@Slf4j
public class NotabeneTransferPublisher {

    // Singleton eager initialization
    private static final NotabeneTransferPublisher INSTANCE = new NotabeneTransferPublisher();

    private NotabeneTransferPublisher() {
        // Private constructor to prevent instantiation.
    }

    public static NotabeneTransferPublisher getInstance() {
        return INSTANCE;
    }

    private final Map<String, NotabeneTransferUpdateListener> listeners = new ConcurrentHashMap<>();

    /**
     * Registers a new listener to receive transfer update events.
     *
     * @param vaspDid  DID of the VASP to register the listener for.
     * @param listener the {@link NotabeneTransferUpdateListener} to be registered.
     */
    public void registerListener(String vaspDid, NotabeneTransferUpdateListener listener) {
        listeners.put(vaspDid, listener);
    }

    /**
     * Unregisters a previously registered listener, stopping it from receiving transfer update events.
     *
     * @param vaspDid DID of the VASP to unregister a listener for.
     */
    public void unregisterListener(String vaspDid) {
        listeners.remove(vaspDid);
    }

    /**
     * Publishes a transfer update event to all registered listeners.
     *
     * @param transferInfo The updated {@link NotabeneTransferInfo} containing the transfer details.
     */
    void publishEvent(NotabeneTransferInfo transferInfo) {
        boolean isSingleVasp = Objects.equals(transferInfo.getOriginatorVaspDid(), transferInfo.getBeneficiaryVaspDid());
        if (isSingleVasp) {
            publishEventForVasp(transferInfo.getOriginatorVaspDid(), transferInfo, "originator & beneficiary");
        } else {
            publishEventForVasp(transferInfo.getOriginatorVaspDid(), transferInfo, "originator");
            publishEventForVasp(transferInfo.getBeneficiaryVaspDid(), transferInfo, "beneficiary");
        }
    }

    private void publishEventForVasp(String vaspDid, NotabeneTransferInfo transferInfo, String vaspRole) {
        if (vaspDid == null) {
            return;
        }

        NotabeneTransferUpdateListener listener = listeners.get(vaspDid);
        if (listener == null) {
            return;
        }

        if (transferInfo.getTransactionRef() == null) {
            log.debug("Skipping transfer update event {} for VASP {} ({}) - missing transactionRef.", transferInfo.getId(), vaspDid, vaspRole);
            return;
        }

        log.debug("Publishing update event {} of transfer {} for VASP {} ({}).", transferInfo.getId(), transferInfo.getTransactionRef(), vaspDid, vaspRole);
        listener.onTransferUpdate(transferInfo);
    }

}
