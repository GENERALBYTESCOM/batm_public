package com.generalbytes.batm.server.extensions.extra.bitcoin;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.coinutil.Bech32;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

public class BitcoinAddressValidator implements ICryptoAddressValidator {
    @Override
    public boolean isAddressValid(String address) {
        try {
            if (address.startsWith("1") || address.startsWith("3")) {
                Base58.decodeToBigInteger(address);
                Base58.decodeChecked(address);
                return true;
            } else if (address.toLowerCase().startsWith("bc1")) {
                Bech32.bech32Decode(address);
                return true;
            }
        } catch (AddressFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean mustBeBase58Address() {
        return false;
    }

    @Override
    public boolean isPaperWalletSupported() {
        return false;
    }
}
