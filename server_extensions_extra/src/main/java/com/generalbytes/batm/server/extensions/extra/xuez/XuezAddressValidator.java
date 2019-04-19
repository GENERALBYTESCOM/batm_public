package com.generalbytes.batm.server.extensions.extra.xuez;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XuezAddressValidator implements ICryptoAddressValidator {

    private static final Logger LOG = LoggerFactory.getLogger(XuezAddressValidator.class);

    @Override
    public boolean isAddressValid(String address) {
        //starts with s1, s3, zc or zs ##TODO  
        if (address.startsWith("s1") || address.startsWith("s3") || address.startsWith("zc") || address.startsWith("zs")) {
            try {
                Base58.decodeToBigInteger(address);
                Base58.decodeChecked(address);
                return true;
            } catch (AddressFormatException e) {
                LOG.error("Error", e);
            }
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
