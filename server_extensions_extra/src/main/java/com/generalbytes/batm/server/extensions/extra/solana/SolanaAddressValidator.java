package com.generalbytes.batm.server.extensions.extra.solana;

import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import lombok.extern.slf4j.Slf4j;

/**
 * Validator for Solana addresses.
 */
@Slf4j
public class SolanaAddressValidator implements ICryptoAddressValidator {

    @Override
    public boolean isAddressValid(String address) {
        try {
            byte[] decodedAddress = Base58.decode(address);
            if (decodedAddress.length == 32) {
                return true;
            }
        } catch (Exception e) {
            log.info("Invalid Solana address format.");
        }
        return false;
    }

    @Override
    public boolean mustBeBase58Address() {
        return true;
    }

    @Override
    public boolean isPaperWalletSupported() {
        return false;
    }

}
