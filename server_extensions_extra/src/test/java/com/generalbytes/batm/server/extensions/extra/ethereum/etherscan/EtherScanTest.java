package com.generalbytes.batm.server.extensions.extra.ethereum.etherscan;

import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

@Ignore
public class EtherScanTest {

    @Test
    public void getAddressBalance() throws IOException {
        ReceivedAmount addressBalance = new EtherScan("...")
            .getAddressBalance("0xa60d67f6e24a71fd5a913c8423675d1fb4907956", "ETH");
        System.out.println(addressBalance.getTotalAmountReceived().toPlainString());
        System.out.println(addressBalance.getConfirmations());
    }

    @Test
    public void getTokenAddressBalance() throws IOException {
        ReceivedAmount addressBalance = new EtherScan("...")
            .getAddressBalance("0xcfeda4d1062e92e341ef6ac8bdce64123c836058", "BAT");
        System.out.println(addressBalance.getTotalAmountReceived().toPlainString());
        System.out.println(addressBalance.getConfirmations());
    }
}