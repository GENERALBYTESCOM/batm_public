package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto.CryptXCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto.CryptXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

import java.util.Map;

public class CryptXWithUniqueAddresses extends CryptXWallet implements IGeneratesNewDepositCryptoAddress {

    private static final Logger log = LoggerFactory.getLogger(CryptXWithUniqueAddresses.class);

    public CryptXWithUniqueAddresses(String scheme, String host, int port, String token, String walletId, String priority) {
        super(scheme, host, port, token, walletId, priority);
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
            CryptXCreateAddressRequest request = new CryptXCreateAddressRequest(label);
            String apiCryptocurrency = getAPICryptocurrency(cryptoCurrency);
            CryptoCurrency currency = CryptoCurrency.valueOfCode(apiCryptocurrency);

            if (currency == CryptoCurrency.ETH) {
                request.setAddressFormat(null);
            }

            Map<String, Object> address = api.createAddress(apiCryptocurrency, this.walletId, request);

            checkForSuccess(address);

            Object addressObj = address.get("address");
            if (addressObj == null || !(addressObj instanceof String)) {
                return null;
            }

            return (String) addressObj;
        } catch (HttpStatusIOException hse) {
            log.debug("create address error: {}", hse.getHttpBody());
        } catch (CryptXException e) {
            log.debug("create address error: {}", e.getErrorMessage());
        } catch (Exception e) {
            log.error("create address error", e);
        }
        return null;
    }

}
