package com.generalbytes.batm.server.extensions.extra;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IGeneratesNewDepositCryptoAddress;
import com.generalbytes.batm.server.extensions.IQueryableWallet;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitcoind.BATMBitcoindRPCWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.bitcoind.BATMBitcoindRPCWalletWithUniqueAddresses;
import com.generalbytes.batm.server.extensions.extra.bitcoincash.BitcoinCashRPCWallet;
import com.generalbytes.batm.server.extensions.extra.bitcoincash.BitcoinCashUniqueAddressRPCWallet;
import com.generalbytes.batm.server.extensions.extra.litecoin.wallets.litecoind.LitecoindRPCWallet;
import com.generalbytes.batm.server.extensions.extra.litecoin.wallets.litecoind.LitecoindUniqueAddressRPCWallet;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

// All tests @Ignore'd here because they depend on external resources (locally running wallets).
// To be run manually, not as a part of the build
@Disabled
class WalletTest {

    public static Object[][] getTestData() {
        return new Object[][]{
            {CryptoCurrency.BCH, new BitcoinCashRPCWallet("http://user:password@localhost:9332", "")},
            {CryptoCurrency.BCH, new BitcoinCashRPCWallet("http://user:password@localhost:9332", "BATMTEST")},
            {CryptoCurrency.BCH, new BitcoinCashUniqueAddressRPCWallet("http://user:password@localhost:9332")},

            {CryptoCurrency.LTC, new LitecoindRPCWallet("http://user:password@localhost:10332", "")},
            {CryptoCurrency.LTC, new LitecoindRPCWallet("http://user:password@localhost:10332", "BATMTEST")},
            {CryptoCurrency.LTC, new LitecoindUniqueAddressRPCWallet("http://user:password@localhost:10332")},

            {CryptoCurrency.BTC, new BATMBitcoindRPCWallet("http://user:b999524f11318c0c86d5b51b3beffbc02b@localhost:8332", "")},
            {CryptoCurrency.BTC, new BATMBitcoindRPCWallet("http://user:b999524f11318c0c86d5b51b3beffbc02b@localhost:8332", "BATMTEST")},
            {CryptoCurrency.BTC, new BATMBitcoindRPCWalletWithUniqueAddresses("http://user:b999524f11318c0c86d5b51b3beffbc02b@localhost:8332")},
        };
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void testSendCoins(String cryptoCurrency, IWallet wallet) {
        String tx = wallet.sendCoins(wallet.getCryptoAddress(cryptoCurrency), new BigDecimal("999"), cryptoCurrency, "test send to self");
        System.out.println(tx);
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void testGetCryptoAddress(String cryptoCurrency, IWallet wallet) {
        String a = wallet.getCryptoAddress(cryptoCurrency);
        assertNotNull(a);
        System.out.println(a);
        String b = wallet.getCryptoAddress(cryptoCurrency);
        assertNotNull(b);
        System.out.println(b);
        assertEquals(a, "getCryptoAddress must return the same address every time", b);
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void testGenerateNewDepositCryptoAddress(String cryptoCurrency, IWallet wallet) {
        if (wallet instanceof IGeneratesNewDepositCryptoAddress) {
            String a = ((IGeneratesNewDepositCryptoAddress) wallet).generateNewDepositCryptoAddress(cryptoCurrency, "testtxid1");
            assertNotNull(a);
            System.out.println(a);
            String b = ((IGeneratesNewDepositCryptoAddress) wallet).generateNewDepositCryptoAddress(cryptoCurrency, "testtxid1");
            assertNotNull(b);
            System.out.println(b);
            assertNotEquals(a, b, "generateNewDepositCryptoAddress must return new address every time");
        }
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void testGetCryptoBalance(String cryptoCurrency, IWallet wallet) {
        BigDecimal cryptoBalance = wallet.getCryptoBalance(cryptoCurrency);
        System.out.println(cryptoBalance);
        assertNotNull(cryptoBalance);
    }

    @ParameterizedTest
    @MethodSource("getTestData")
    void testReceivedAmount(String cryptoCurrency, IWallet wallet) {
        if (wallet instanceof IQueryableWallet) {
            ReceivedAmount amount = ((IQueryableWallet) wallet).getReceivedAmount("MAtZDadQRhRaD2uAZ8gUMHq8Say4TSVshw", cryptoCurrency);
            System.out.println(amount.getConfirmations());
            System.out.println(amount.getTotalAmountReceived());
        }
    }

}