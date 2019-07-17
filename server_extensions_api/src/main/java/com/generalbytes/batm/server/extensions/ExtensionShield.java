package com.generalbytes.batm.server.extensions;

import com.generalbytes.batm.server.extensions.aml.IExternalIdentity;
import com.generalbytes.batm.server.extensions.aml.IExternalIdentityProvider;
import com.generalbytes.batm.server.extensions.watchlist.IWatchList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class ExtensionShield implements IExtension {
    private static final Logger log = LoggerFactory.getLogger("batm.master.ExtensionShield");

    private IExtension extension;

    public ExtensionShield(IExtension extension) {
        this.extension = extension;
    }

    @Override
    public void init(IExtensionContext ctx) {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "init";
        log.trace(name + " called.");

        extension.init(ctx);

        long duration = System.currentTimeMillis() - now;
        log.trace(name + " finished in " + duration +" ms");
    }

    @Override
    public String getName() {
        return extension.getName();
    }

    @Override
    public Set<String> getSupportedCryptoCurrencies() {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "getSupportedCryptoCurrencies";
        log.trace(name + " called.");
        try {
            return extension.getSupportedCryptoCurrencies();
        }finally {
            long duration = System.currentTimeMillis() - now;
            log.trace(name + " finished in " + duration + " ms");
        }
    }

    @Override
    public Set<ICryptoCurrencyDefinition> getCryptoCurrencyDefinitions() {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "getCryptoCurrencyDefinitions";
        log.trace(name + " called.");

        try {
            return extension.getCryptoCurrencyDefinitions();
        }finally {
            long duration = System.currentTimeMillis() - now;
            log.trace(name + " finished in " + duration +" ms");
        }
    }

    @Override
    public IExchange createExchange(String exchangeLogin) {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "createExchange";
        log.trace(name + " called.");
        try {
            return extension.createExchange(exchangeLogin);
        }finally {
            long duration = System.currentTimeMillis() - now;
            log.trace(name + " finished in " + duration +" ms");
        }
    }

    @Override
    public IPaymentProcessor createPaymentProcessor(String paymentProcessorLogin) {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "createPaymentProcessor";
        log.trace(name + " called.");
        try {
            return extension.createPaymentProcessor(paymentProcessorLogin);
        }finally {
            long duration = System.currentTimeMillis() - now;
            log.trace(name + " finished in " + duration +" ms");
        }
    }

    @Override
    public IRateSource createRateSource(String sourceLogin) {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "createRateSource";
        log.trace(name + " called.");
        try {
            return extension.createRateSource(sourceLogin);
        }finally {
            long duration = System.currentTimeMillis() - now;
            log.trace(name + " finished in " + duration + " ms");
        }
    }

    @Override
    public IWallet createWallet(String walletLogin) {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "createWallet";
        log.trace(name + " called.");
        try {
            return extension.createWallet(walletLogin);
        }finally {
            long duration = System.currentTimeMillis() - now;
            log.trace(name + " finished in " + duration + " ms");
        }
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "createAddressValidator";
        log.trace(name + " called.");
        try {
            return extension.createAddressValidator(cryptoCurrency);
        }finally {
            long duration = System.currentTimeMillis() - now;
            log.trace(name + " finished in " + duration + " ms");
        }
    }

    @Override
    public IPaperWalletGenerator createPaperWalletGenerator(String cryptoCurrency) {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "createPaperWalletGenerator";
        log.trace(name + " called.");
        try {
            return extension.createPaperWalletGenerator(cryptoCurrency);
        }finally {
            long duration = System.currentTimeMillis() - now;
            log.trace(name + " finished in " + duration + " ms");
        }
    }

    @Override
    public Set<String> getSupportedWatchListsNames() {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "getSupportedWatchListsNames";
        log.trace(name + " called.");
        try {
            return extension.getSupportedWatchListsNames();
        }finally {
            long duration = System.currentTimeMillis() - now;
            log.trace(name + " finished in " + duration + " ms");
        }
    }

    @Override
    public IWatchList getWatchList(String watchlistName) {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "getWatchList";
        log.trace(name + " called.");
        try {
            return extension.getWatchList(watchlistName);
        }finally {
            long duration = System.currentTimeMillis() - now;
            log.trace(name + " finished in " + duration + " ms");
        }
    }

    @Override
    public Set<IRestService> getRestServices() {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "getRestServices";
        log.trace(name + " called.");
        try {
            return extension.getRestServices();
        }finally {
            long duration = System.currentTimeMillis() - now;
            log.trace(name + " finished in " + duration + " ms");
        }
    }

    @Override
    public Set<Class> getChatCommands() {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "getChatCommands";
        log.trace(name + " called.");
        try {
            return extension.getChatCommands();
        }finally {
            long duration = System.currentTimeMillis() - now;
            log.trace(name + " finished in " + duration + " ms");
        }
    }

    @Override
    public Set<IExternalIdentityProvider> getIdentityProviders() {
        long now = System.currentTimeMillis();
        String name = extension.getName() + "." + "getIdentityProviders";
        log.trace(name + " called.");
        try {
            Set<IExternalIdentityProvider> orgProviders = extension.getIdentityProviders();
            if (orgProviders != null) {
                Set<IExternalIdentityProvider> identityProviders = new HashSet<>();

                for (IExternalIdentityProvider ip : orgProviders) {
                    identityProviders.add(shieldIP(ip));
                }
                return identityProviders;
            }
            return null;
        }finally {
            long duration = System.currentTimeMillis() - now;
            log.trace(name + " finished in " + duration + " ms");
        }
    }

    private IExternalIdentityProvider shieldIP(IExternalIdentityProvider ip) {
        if (ip != null) {
            return new IExternalIdentityProvider() {
                @Override
                public IExternalIdentity findIdentityByExternalId(String identityExternalId) {
                    long now = System.currentTimeMillis();
                    String name = extension.getName() + "." + "findIdentityByExternalId " + identityExternalId;
                    log.trace(name + " called.");
                    try {
                        return ip.findIdentityByExternalId(identityExternalId);
                    }finally {
                        long duration = System.currentTimeMillis() - now;
                        log.trace(name + " finished in " + duration + " ms");
                    }
                }

                @Override
                public IExternalIdentity findIdentityByPhoneNumber(String cellPhoneNumber) {
                    long now = System.currentTimeMillis();
                    String name = extension.getName() + "." + "findIdentityByPhoneNumber " + cellPhoneNumber;
                    log.trace(name + " called.");
                    try {
                        return ip.findIdentityByPhoneNumber(cellPhoneNumber);
                    }finally {
                        long duration = System.currentTimeMillis() - now;
                        log.trace(name + " finished in " + duration + " ms");
                    }
                }

                @Override
                public IExternalIdentity findIdentityByEmail(String emailAddress) {
                    long now = System.currentTimeMillis();
                    String name = extension.getName() + "." + "findIdentityByEmail " + emailAddress;
                    log.trace(name + " called.");
                    try {
                        return ip.findIdentityByEmail(emailAddress);
                    }finally {
                        long duration = System.currentTimeMillis() - now;
                        log.trace(name + " finished in " + duration + " ms");
                    }
                }

                @Override
                public boolean isPINCorrect(String identityExternalId, String pinEnteredByCustomer) {
                    long now = System.currentTimeMillis();
                    String name = extension.getName() + "." + "isPINCorrect " + identityExternalId;
                    log.trace(name + " called.");
                    try {
                        return ip.isPINCorrect(identityExternalId, pinEnteredByCustomer);
                    }finally {
                        long duration = System.currentTimeMillis() - now;
                        log.trace(name + " finished in " + duration + " ms");
                    }
                }
            };
        }
        return null;
    }


}
