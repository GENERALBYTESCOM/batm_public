package com.generalbytes.batm.server.extensions.extra.test.shadowcash;

import com.generalbytes.batm.server.extensions.ICurrencies;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.extra.shadowcash.wallets.paperwallet.ShadowcashPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.extra.test.BaseTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author ludx
 */
public class ShadowcashPaperWalletTest extends BaseTest {

    private ShadowcashPaperWalletGenerator walletGenerator;

    @BeforeClass
    public void beforeClass() {
        walletGenerator = new ShadowcashPaperWalletGenerator();
    }

    @Test(groups = {"sdcpaperwallet"})
    public void mockGenerateWalletTest() throws IOException {
        IPaperWallet shadowcashPaperWallet = walletGenerator.generateWallet(ICurrencies.SDC, "password", "en");
        System.out.println("privateKey: " + shadowcashPaperWallet.getPrivateKey());
        System.out.println("address: " + shadowcashPaperWallet.getAddress());
        System.out.println("message: " + shadowcashPaperWallet.getMessage());
        assertThat(shadowcashPaperWallet.getMessage(), is(walletGenerator.getMessage(Locale.ENGLISH.getLanguage())));
    }

}