package com.generalbytes.batm.server.extensions.coinutil;

import com.generalbytes.bitrafael.tools.wallet.bch.Bech32;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.params.MainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BCHUtil {
    private static final Logger log = LoggerFactory.getLogger(BCHUtil.class);

    /**
     * Converts "bech32" (starts with q) to "3*" (starts with 1)
     *
     * @param originalDestinationAddress
     * @return converted address or the original if input is not bech32
     */
    public static String convertBech32To3(String originalDestinationAddress) {
        try {
            if ((originalDestinationAddress.contains(":") && originalDestinationAddress.startsWith("bitcoincash:q"))
                || (!originalDestinationAddress.contains(":") && originalDestinationAddress.startsWith("q"))) {
                return LegacyAddress.fromPubKeyHash(MainNetParams.get(), Bech32.decodeCashAddress(originalDestinationAddress)).toBase58();
            }
        } catch (Exception e) {
            log.error("failed to convert address '{}'", originalDestinationAddress, e);
        }
        return originalDestinationAddress;
    }
}
