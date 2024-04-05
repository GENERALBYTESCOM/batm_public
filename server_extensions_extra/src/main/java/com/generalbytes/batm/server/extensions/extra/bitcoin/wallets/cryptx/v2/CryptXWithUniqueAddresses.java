package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto.CryptXCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto.CryptXException;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.cryptx.v2.dto.CryptXReceivedAmount;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.HttpStatusIOException;

import java.math.BigInteger;
import java.util.Map;

public class CryptXWithUniqueAddresses extends CryptXWallet implements IGeneratesNewDepositCryptoAddress, IQueryableWallet {

    private static final Logger log = LoggerFactory.getLogger(CryptXWithUniqueAddresses.class);

    public CryptXWithUniqueAddresses(String scheme, String host, int port, String token, String walletId, String priority, String customFeePrice, String customGasLimit, String password) {
        super(scheme, host, port, token, walletId, priority, customFeePrice, customGasLimit, password);
    }

    @Override
    public String generateNewDepositCryptoAddress(String cryptoCurrency, String label) {
        if (cryptoCurrency == null) {
            cryptoCurrency = getPreferredCryptoCurrency();
        }
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.warn("{} not supported", cryptoCurrency);
            return null;
        }
        cryptoCurrency = cryptoCurrency.toLowerCase();
        try {
            CryptXCreateAddressRequest request = new CryptXCreateAddressRequest(label, password);
            String apiCryptocurrency = getAPICryptocurrency(cryptoCurrency);
            CryptoCurrency currency = CryptoCurrency.valueOfCode(apiCryptocurrency);

            if (currency == CryptoCurrency.ETH) {
                request.setAddressFormat(null);
            }

            Map<String, Object> address = api.createAddress(cryptoCurrency, this.walletId, request);
            return (String) address.get("address");
        } catch (HttpStatusIOException hse) {
            log.debug("create address error: {}", hse.getHttpBody());
        } catch (CryptXException e) {
            log.debug("create address error: {}", e.getErrorMessage());
        } catch (Exception e) {
            log.error("create address error", e);
        }
        return null;
    }

    @Override
    public ReceivedAmount getReceivedAmount(String address, String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            log.warn("{} not supported", cryptoCurrency);
            return ReceivedAmount.ZERO;
        }
        cryptoCurrency = cryptoCurrency.toLowerCase();

        try {
            CryptXReceivedAmount cryptXReceivedAmount = api.getReceivedAmount(cryptoCurrency, this.walletId, address);
            if (cryptXReceivedAmount.getAmount().compareTo(BigInteger.ZERO) > 0) {
                return new ReceivedAmount(toMajorUnit(cryptoCurrency, cryptXReceivedAmount.getAmount().toString()), 999);
            }
            return new ReceivedAmount(toMajorUnit(cryptoCurrency, cryptXReceivedAmount.getAmount().toString()), 0);
        } catch (HttpStatusIOException hse) {
            log.debug("get received amount error: {}", hse.getHttpBody());
        } catch (CryptXException e) {
            log.debug("get received amount error: {}", e.getErrorMessage());
        } catch (Exception e) {
            log.error("get received amount error", e);
        }

        return ReceivedAmount.ZERO;
    }

}
