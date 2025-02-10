package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IWalletInformation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.security.GeneralSecurityException;

class LndWalletTest {

    private LndWallet w;

    public LndWalletTest() throws GeneralSecurityException {
        w = new LndWallet("https://localhost:8080/", "0201036C6E6402CF01030A104B6A274FD380DE6103954B40555DB71C1201301A160A0761646472657373120472656164120577726974651A130A04696E666F120472656164120577726974651A170A08696E766F69636573120472656164120577726974651A160A076D657373616765120472656164120577726974651A170A086F6666636861696E120472656164120577726974651A160A076F6E636861696E120472656164120577726974651A140A057065657273120472656164120577726974651A120A067369676E6572120867656E657261746500000620E52BA8D3D646C5662EEFE5CF04691CA6924E58C791CAF6BB29C3CFFB485E0815", "2D2D2D2D2D424547494E2043455254494649434154452D2D2D2D2D0A4D49494239544343415A71674177494241674952414B2F61774F7143625A4C6866776E364768582F4C4A3877436759494B6F5A497A6A3045417749774E6A45660A4D4230474131554543684D576247356B494746316447396E5A57356C636D46305A57516759325679644445544D4245474131554541784D4B63485A35614735680A62433177597A4165467730784F5441334D4467784E4449304D7A4A61467730794D4441354D4445784E4449304D7A4A614D445978487A416442674E5642416F540A466D78755A434268645852765A3256755A584A686447566B49474E6C636E5178457A415242674E5642414D54436E4232655768755957777463474D77575441540A42676371686B6A4F5051494242676771686B6A4F50514D4242774E43414151434B7A5232773459796A6E4351582B77496F4639356C68326A6B324476514E5A480A62593045686B385A7955474F6C78447154334957625348466344524C77494F636C30547A44687031744A7857536E6B7473377A446F3447494D4947464D4134470A41315564447745422F775145417749437044415042674E5648524D4241663845425441444151482F4D47494741315564455152624D466D43436E4232655768750A5957777463474F4343577876593246736147397A64494945645735706549494B64573570654842685932746C64496345667741414159635141414141414141410A41414141414141414141414141596345774B677A794963512F6F41414141414141414479472B76776E64704D6244414B42676771686B6A4F5051514441674E4A0A41444247416945416F557858736E342B4172786B76364F7A466F70454830762F6C55695670616F31366E35436B536755625073434951436B35443555596D356A0A513170443769617A556C4E717856714D346B5270334F7276782F5936504753674B513D3D0A2D2D2D2D2D454E442043455254494649434154452D2D2D2D2D0A", "5");
    }

    @Disabled
    @Test
    void sendCoins() {
        IWalletInformation i = w.getWalletInformation();
        String paymentHash = w.sendCoins("LNBC1U1PWJMJJNPP5YRDV8EPPK74UZ69QVHK940EHE9469H8GHXE626MZGCLZ202REZKQDPY2PKXZ7FQVYSXWCTDV5SX7E3QWD3HYCT5VD5QCQZPGQNL8L0227LDNLEJA3HQWUFHF788ADMD640YKFZ8A9FAGYG4RQE7XGC4CXFMTVU8SAWPE3WVU8WNUHW52R6LSD4797RZ0DPMPTHH3K0CQ378HK2",
            new BigDecimal("0.000001"), CryptoCurrency.LBTC.getCode(), "R23V4C");
        System.out.println(paymentHash);
    }

    @Disabled
    @Test
    void getCryptoAddress() {
        String a = w.getCryptoAddress("LBTC");
        System.out.println(a);
        String i = w.getInvoice(new BigDecimal("0.0001"), "LBTC", 1000l, "test invoice");
        System.out.println(i);
    }

    @Disabled
    @Test
    void getCryptoBalance() {
        BigDecimal cryptoBalance = w.getCryptoBalance(CryptoCurrency.LBTC.getCode());
        System.out.println(cryptoBalance);
    }

    @Disabled
    @Test
    void receive() {
        String invoice = w.getInvoice(new BigDecimal("0.00000003"), CryptoCurrency.LBTC.getCode(), 1000l, "test");
        System.out.println(invoice);
        System.out.println(w.getReceivedAmount(invoice, "LBTC"));
    }
}