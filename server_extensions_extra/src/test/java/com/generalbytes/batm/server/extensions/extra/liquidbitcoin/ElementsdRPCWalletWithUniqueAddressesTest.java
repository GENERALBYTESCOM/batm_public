package com.generalbytes.batm.server.extensions.extra.liquidbitcoin;

import com.generalbytes.batm.server.extensions.extra.common.RPCClient;
import com.generalbytes.batm.server.extensions.extra.liquidbitcoin.wallets.elementsd.ElementsdRPCClient;
import com.generalbytes.batm.server.extensions.extra.liquidbitcoin.wallets.elementsd.ElementsdRPCWalletWithUniqueAddresses;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ElementsdRPCWalletWithUniqueAddressesTest {

    @Test
    void testCreateClient() {
        ElementsdRPCWalletWithUniqueAddresses wallet = new ElementsdRPCWalletWithUniqueAddresses("http://user:pass@localhost:8332", "walletName");
        RPCClient client = wallet.getClient();
        assertInstanceOf(ElementsdRPCClient.class, client);
    }

    @Test
    void testCreateClient_invalidUrl() {
        ElementsdRPCWalletWithUniqueAddresses wallet = new ElementsdRPCWalletWithUniqueAddresses("invalidRpcUrl", "walletName");
        RPCClient client = wallet.getClient();
        assertNull(client);
    }
}

