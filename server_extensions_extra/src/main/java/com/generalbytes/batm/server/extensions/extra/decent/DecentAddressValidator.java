package com.generalbytes.batm.server.extensions.extra.decent;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

/**
 * Created by maros on 12.7.2017.
 */

public class DecentAddressValidator implements ICryptoAddressValidator {

    @Override
    public boolean isAddressValid(String address) {
        if (address.startsWith("U") || address.startsWith("u")) {
            try {
                Base58.decodeToBigInteger(address);
                Base58.decodeChecked(address);
            } catch (AddressFormatException e) {
                e.printStackTrace();
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
