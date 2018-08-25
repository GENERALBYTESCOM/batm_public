package com.generalbytes.batm.server.extensions.extra.anon;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

/**
 * Created by ANON Core Developers on 8/25/18.
 */
public class ANONAddressValidator implements ICryptoAddressValidator {
    @Override
    public boolean isAddressValid(String address) {
        if (address.startsWith("An") || address.startsWith("zk")) {
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
