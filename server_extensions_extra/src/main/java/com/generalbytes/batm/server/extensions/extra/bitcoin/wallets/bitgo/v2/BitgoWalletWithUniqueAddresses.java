package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2;

import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.BitGoCreateAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitgo.v2.dto.ErrorResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

public class BitgoWalletWithUniqueAddresses extends BitgoWallet implements IGeneratesNewDepositCryptoAddress {
    private static final Logger log = LoggerFactory.getLogger(BitgoWalletWithUniqueAddresses.class);

    public BitgoWalletWithUniqueAddresses(String host, String port, String token, String walletId, String walletPassphrase) {
        super(host, port, token, walletId, walletPassphrase);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        if (cryptoCurrency == null) {
            cryptoCurrency = getPreferredCryptoCurrency();
        }
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        cryptoCurrency = cryptoCurrency.toLowerCase();
        try {

            final BitGoCreateAddressRequest request = new BitGoCreateAddressRequest();
            request.setChain(0); // https://github.com/BitGo/unspents/blob/master/src/codes.ts ??? [0, UnspentType.p2sh, Purpose.external],
            request.setLabel(label);
            final BitGoCreateAddressResponse response = api.createAddress(cryptoCurrency, walletId, request);
            if (response == null) {
                return null;
            }
            String address = response.getAddress();
            if (address == null || address.isEmpty()) {
                return null;
            }
            return address;
        } catch (HttpStatusIOException hse) {
            log.debug("create address error: {}", hse.getHttpBody());
        } catch (ErrorResponseException e) {
            log.debug("create address error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("create address error", e);
        }
        return null;
    }
}
