package com.generalbytes.batm.server.extensions.extra.tron;

import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.trident.utils.Base58Check;

class TronCryptoAddressValidator implements ICryptoAddressValidator {
    private static final Logger log = LoggerFactory.getLogger(TronCryptoAddressValidator.class);


    @Override
    public boolean isAddressValid(String address) {
        if (address == null || !address.startsWith("T")) {
            return false;
        }

        try {
            Base58Check.base58ToBytes(address);
            return true;
        } catch (RuntimeException e) {
            log.warn("Address not valid", e);
            return false;
        }

    }

    @Override
    public boolean isPaperWalletSupported() {
        return false;
    }

    @Override
    public boolean mustBeBase58Address() {
        return false;
    }

}
