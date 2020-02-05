package com.generalbytes.batm.server.extensions.extra.primecoin;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.coinutil.Base58;

public class PrimecoinAddressValidator implements ICryptoAddressValidator {

    @Override
    public boolean isAddressValid(String address) {
        try {
            byte[] addressBytes = Base58.decode(address);
            if (addressBytes.length != 25) {
                return false;
            }
            if (addressBytes[0] != 23 /*MainNet*/ && addressBytes[0] != 111 /*TestNet*/) {
                return false;
            }
            Base58.decodeChecked(address);
            return true;
        } catch (AddressFormatException ex) {
            return false;
        }
    }

    @Override
    public boolean mustBeBase58Address() {
        return true;
    }

    @Override
    public boolean isPaperWalletSupported() {
        return true;
    }
}
