package com.generalbytes.batm.server.extensions.extra.aquanow;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AquanowAddressValidator implements ICryptoAddressValidator {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.AquanowAddressValidator");

    @Override
    public boolean isAddressValid(String address) {
        String optionalPrefix = "aquanow:";
        if (address.startsWith(optionalPrefix)) {
            address = address.substring(optionalPrefix.length(), address.length());
        }
        if (address.startsWith("S")) {
            try {
                Base58.decodeToBigInteger(address);
                Base58.decodeChecked(address);
            } catch (AddressFormatException e) {
                log.error("Error", e);
                return false;
            }
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean isPaperWalletSupported() {
        return false;
    }

    @Override
    public boolean mustBeBase58Address() {
        return true;
    }
}
