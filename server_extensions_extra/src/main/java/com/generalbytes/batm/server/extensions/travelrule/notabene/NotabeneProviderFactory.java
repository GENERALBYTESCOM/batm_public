package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProvider;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderFactory;
import com.generalbytes.batm.server.extensions.travelrule.TravelRuleExtensionContext;

import java.util.HashMap;
import java.util.Map;

public class NotabeneProviderFactory implements ITravelRuleProviderFactory {

    private final NotabeneService notabeneService;
    private final NotabeneIncomingTransferService notabeneIncomingTransferService;
    private final NotabeneTransferPublisher notabeneTransferPublisher;
    private final NotabeneAuthService notabeneAuthService;
    private final NotabeneConfiguration configuration;

    /**
     * Already initialized travel rule providers. VASP DID is used as a key to avoid multiple initializations based on same settings.
     */
    private final Map<String, NotabeneTravelRuleProvider> travelRuleProviders = new HashMap<>();

    public NotabeneProviderFactory(NotabeneConfiguration configuration, TravelRuleExtensionContext extensionContext) {
        this.configuration = configuration;
        notabeneTransferPublisher = NotabeneTransferPublisher.getInstance();

        NotabeneApiFactory notabeneApiFactory = new NotabeneApiFactory(configuration);
        notabeneAuthService = new NotabeneAuthService(notabeneApiFactory, configuration);
        NotabeneApiService notabeneApiService = new NotabeneApiService(notabeneAuthService);
        NotabeneApiWrapper notabeneApiWrapper = new NotabeneApiWrapper(notabeneApiFactory, notabeneApiService);
        notabeneService = new NotabeneService(notabeneApiWrapper, configuration);
        notabeneIncomingTransferService = new NotabeneIncomingTransferService(notabeneService, extensionContext);
    }

    @Override
    public String getProviderName() {
        return NotabeneTravelRuleProvider.NAME;
    }

    @Override
    public synchronized ITravelRuleProvider getProvider(ITravelRuleProviderCredentials credentials) {
        String vaspDid = credentials.getVaspDid();
        if (vaspDid == null) {
            return initializeProvider(credentials);
        }
        if (travelRuleProviders.containsKey(vaspDid)) {
            NotabeneTravelRuleProvider travelRuleProvider = travelRuleProviders.get(vaspDid);
            // Update the credentials in case they have changed.
            // If we didn't do this, the provider would be stuck with the credentials it got at initialization.
            travelRuleProvider.updateCredentials(credentials);
            return travelRuleProvider;
        }
        NotabeneTravelRuleProvider notabeneTravelRuleProvider = initializeProvider(credentials);
        travelRuleProviders.put(vaspDid, notabeneTravelRuleProvider);

        return notabeneTravelRuleProvider;
    }

    private NotabeneTravelRuleProvider initializeProvider(ITravelRuleProviderCredentials credentials) {
        return new NotabeneTravelRuleProvider(credentials,
            configuration,
            notabeneAuthService,
            notabeneService,
            notabeneIncomingTransferService,
            notabeneTransferPublisher);
    }
}
