package com.generalbytes.batm.server.extensions.extra.nubits;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by woolly_sammoth on 11/12/14.
 */
public class NubitsAddressValidator implements ICryptoAddressValidator {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.NubitsAddressValidator");
    @Override
    public boolean isAddressValid(String address) {
        if (address.startsWith("B")) {
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
