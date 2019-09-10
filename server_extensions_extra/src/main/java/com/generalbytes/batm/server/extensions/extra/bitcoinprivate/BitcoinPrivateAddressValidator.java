package com.generalbytes.batm.server.extensions.extra.bitcoinprivate;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Base58;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Pega88 on 6/8/18.
 */
public class BitcoinPrivateAddressValidator implements ICryptoAddressValidator {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.BitcoinPrivateAddressValidator");
    @Override
    public boolean isAddressValid(String address) {
        if (address.startsWith("b1") || address.startsWith("bx") || address.startsWith("zk")) {
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
