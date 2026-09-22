package com.generalbytes.batm.server.extensions.extra.sui;

import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Validates SUI addresses: "0x" prefix + 64 lowercase hex characters (32 bytes).
 */
public class SuiAddressValidator implements ICryptoAddressValidator {

    private static final Logger log = LoggerFactory.getLogger(SuiAddressValidator.class);

    @Override
    public boolean isAddressValid(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }
        if (address.matches("^0x[0-9a-fA-F]{64}$")) {
            return true;
        }
        log.info("Invalid SUI address format: {}", address);
        return false;
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
