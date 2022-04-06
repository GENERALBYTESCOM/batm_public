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
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;

// All tests @Ignore'd here because they depend on external resources (locally running wallets).
// To be run manually, not as a part of the build
@Ignore
@RunWith(Parameterized.class)
public class WalletTest {
    private final String cryptoCurrency;
    private final IWallet wallet;

    @Parameterized.Parameters
    public static Collection getTestData() {
        return Arrays.asList(new Object[][]{
            {CryptoCurrency.BCH, new BitcoinCashRPCWallet("http://user:password@localhost:9332", "")},
            {CryptoCurrency.BCH, new BitcoinCashRPCWallet("http://user:password@localhost:9332", "BATMTEST")},
            {CryptoCurrency.BCH, new BitcoinCashUniqueAddressRPCWallet("http://user:password@localhost:9332")},

            {CryptoCurrency.LTC, new LitecoindRPCWallet("http://user:password@localhost:10332", "")},
            {CryptoCurrency.LTC, new LitecoindRPCWallet("http://user:password@localhost:10332", "BATMTEST")},
            {CryptoCurrency.LTC, new LitecoindUniqueAddressRPCWallet("http://user:password@localhost:10332")},

            {CryptoCurrency.BTC, new BATMBitcoindRPCWallet("http://user:b999524f11318c0c86d5b51b3beffbc02b@localhost:8332", "")},
            {CryptoCurrency.BTC, new BATMBitcoindRPCWallet("http://user:b999524f11318c0c86d5b51b3beffbc02b@localhost:8332", "BATMTEST")},
            {CryptoCurrency.BTC, new BATMBitcoindRPCWalletWithUniqueAddresses("http://user:b999524f11318c0c86d5b51b3beffbc02b@localhost:8332")},
        });
    }

    // @Parameterized.Parameters annotated method results are passed here
    public WalletTest(CryptoCurrency cryptoCurrency, IWallet wallet) {
        this.cryptoCurrency = cryptoCurrency.getCode();
        this.wallet = wallet;
    }


    @Test
    public void testSendCoins() {
        String tx = wallet.sendCoins(wallet.getCryptoAddress(cryptoCurrency), new BigDecimal("999"), cryptoCurrency, "test send to self");
        System.out.println(tx);
    }

    @Test
    public void testGetCryptoAddress() {
        String a = wallet.getCryptoAddress(cryptoCurrency);
        Assert.assertNotNull(a);
        System.out.println(a);
        String b = wallet.getCryptoAddress(cryptoCurrency);
        Assert.assertNotNull(b);
        System.out.println(b);
        Assert.assertEquals("getCryptoAddress must return the same address every time", a, b);
    }

    @Test
    public void testGenerateNewDepositCryptoAddress() {
        if (wallet instanceof IGeneratesNewDepositCryptoAddress) {
            String a = ((IGeneratesNewDepositCryptoAddress) wallet).generateNewDepositCryptoAddress(cryptoCurrency, "testtxid1");
            Assert.assertNotNull(a);
            System.out.println(a);
            String b = ((IGeneratesNewDepositCryptoAddress) wallet).generateNewDepositCryptoAddress(cryptoCurrency, "testtxid1");
            Assert.assertNotNull(b);
            System.out.println(b);
            Assert.assertFalse("generateNewDepositCryptoAddress must return new address every time", a.equals(b));
        }
    }

    @Test
    public void testGetCryptoBalance() {
        BigDecimal cryptoBalance = wallet.getCryptoBalance(cryptoCurrency);
        System.out.println(cryptoBalance);
        Assert.assertNotNull(cryptoBalance);
    }

    @Test
    public void testReceivedAmount() {
        if (wallet instanceof IQueryableWallet) {
            ReceivedAmount amount = ((IQueryableWallet) wallet).getReceivedAmount("MAtZDadQRhRaD2uAZ8gUMHq8Say4TSVshw", cryptoCurrency);
            System.out.println(amount.getConfirmations());
            System.out.println(amount.getTotalAmountReceived());
        }
    }

}