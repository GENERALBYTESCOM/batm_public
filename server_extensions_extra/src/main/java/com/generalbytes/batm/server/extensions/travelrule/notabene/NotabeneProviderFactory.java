package com.generalbytes.batm.server.extensions.travelrule.notabene;

import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProvider;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderCredentials;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProviderFactory;

import java.util.HashMap;
import java.util.Map;

public class NotabeneProviderFactory implements ITravelRuleProviderFactory {

    private final NotabeneService notabeneService;
    private final NotabeneTransferPublisher notabeneTransferPublisher;
    private final NotabeneAuthService notabeneAuthService;
    private final NotabeneConfiguration configuration;

    /**
     * Already initialized travel rule providers. VASP DID is used as a key to avoid multiple initializations based on same settings.
     */
    private final Map<String, ITravelRuleProvider> travelRuleProviders = new HashMap<>();

    public NotabeneProviderFactory(NotabeneConfiguration configuration) {
        this.configuration = configuration;
        notabeneTransferPublisher = NotabeneTransferPublisher.getInstance();

        NotabeneApiFactory notabeneApiFactory = new NotabeneApiFactory(configuration);
        notabeneAuthService = new NotabeneAuthService(notabeneApiFactory, configuration);
        NotabeneApiService notabeneApiService = new NotabeneApiService(notabeneAuthService);
        NotabeneApiWrapper notabeneApiWrapper = new NotabeneApiWrapper(notabeneApiFactory, notabeneApiService);
        notabeneService = new NotabeneService(notabeneApiWrapper, configuration);
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
            return travelRuleProviders.get(vaspDid);
        }
        NotabeneTravelRuleProvider notabeneTravelRuleProvider = initializeProvider(credentials);
        travelRuleProviders.put(vaspDid, notabeneTravelRuleProvider);

        return notabeneTravelRuleProvider;
    }

    private NotabeneTravelRuleProvider initializeProvider(ITravelRuleProviderCredentials credentials) {
        return new NotabeneTravelRuleProvider(credentials, configuration, notabeneAuthService, notabeneService, notabeneTransferPublisher);
    }
}
