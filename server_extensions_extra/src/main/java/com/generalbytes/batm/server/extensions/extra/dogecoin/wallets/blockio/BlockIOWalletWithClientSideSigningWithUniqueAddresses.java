package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

import java.util.Random;

public class BlockIOWalletWithClientSideSigningWithUniqueAddresses extends BlockIOWalletWithClientSideSigning implements IGeneratesNewDepositCryptoAddress {
    private static final Logger log = LoggerFactory.getLogger(BlockIOWalletWithClientSideSigningWithUniqueAddresses.class);

    public BlockIOWalletWithClientSideSigningWithUniqueAddresses(String apiKey, String pin, String priority) {
        super(apiKey, pin, priority);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        try {
            BlockIOResponseNewAddress response = api.getNewAddress(formatAddressLabel(label));
            if (response != null && response.getData() != null && response.getData().getAddress() != null && !response.getData().getAddress().isEmpty()) {
                return response.getData().getAddress();
            }
        } catch (HttpStatusIOException e) {
            log.error("HTTP error in getUniqueCryptoAddress: {}", e.getHttpBody());
        } catch (Exception e) {
            log.error("Error", e);
        }
        return null;
    }


    private String formatAddressLabel(String label) {
        // label has to be unique and can only contain letters, numbers, _, -, @, ., and !.
        return label.replaceAll(" ", "-").replaceAll("[^-_@.!a-zA-Z0-9]", "") + "-" + new Random().nextInt();
    }
}
