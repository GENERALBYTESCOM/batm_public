package com.generalbytes.batm.server.extensions.extra.nuls;

import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

public class NulsAddressValidator implements ICryptoAddressValidator {

    @Override
    public boolean isAddressValid(String address) {
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
