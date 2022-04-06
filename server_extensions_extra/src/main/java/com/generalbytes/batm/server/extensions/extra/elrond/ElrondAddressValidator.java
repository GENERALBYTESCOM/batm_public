package com.generalbytes.batm.server.extensions.extra.elrond;

import com.generalbytes.batm.server.coinutil.AddressFormatException;
import com.generalbytes.batm.server.coinutil.Bech32;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElrondAddressValidator implements ICryptoAddressValidator {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.ElrondAddressValidator");

    @Override
    public boolean isAddressValid(String address) {
        try {
            Bech32.Bech32Data bech32Data = Bech32.decodeUnlimitedLength(address);
            if (!bech32Data.hrp.equals("erd")) {
                log.info("Address HRP is not 'erd'");
                return false;
            }
            return true;
        } catch (AddressFormatException e) {
            log.error("Cannot decode Bech32 address", e);
            return false;
        }
    }

    @Override
    public boolean isPaperWalletSupported() {
        return false;
    }

    @Override
    public boolean mustBeBase58Address() {
        return false;
    }
}
