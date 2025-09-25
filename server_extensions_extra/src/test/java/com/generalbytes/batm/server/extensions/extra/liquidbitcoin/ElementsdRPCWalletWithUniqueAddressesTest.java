package com.generalbytes.batm.server.extensions.extra.liquidbitcoin;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.extra.common.RPCClient;
import com.generalbytes.batm.server.extensions.extra.liquidbitcoin.wallets.elementsd.ElementsdRPCWalletWithUniqueAddresses;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class ElementsdRPCWalletWithUniqueAddressesTest {
    private static final String ADDRESS_EMPTY = "lq1qqdvx9c57pcdu0j8gmgxnhvhgmfx5slc42smmutfw8jl6sm0aj8nt0n3helrqmdn2an59cqqe6yh4s637zzz8w9s79k9dzhu2v";
    private static final String ADDRESS_WITH_COINS = "lq1qqwch6wp99rxvqzjqahtkzkgtqpp4ujs3p2qqn4r5rexah80jxutq9aflw5qveu7mctht5h2p39j8z6jnfhfqsn5xy9exg3rz6";
    private static final String ADDRESS_WITH_MORE_TXS = "ex1q0u30psmd0v7r5y3l5znvpn9w57sneczjms7m7z";

    private ElementsdRPCWalletWithUniqueAddresses wallet;
    @Mock
    private RPCClient client;

    @BeforeEach
    void setUp() {
        wallet = new ElementsdRPCWalletWithUniqueAddresses("url", "walletName") {
            @Override
            public RPCClient createClient(String cryptoCurrency, String rpcURL) {
                return client;
            }
        };
    }
    @Test
    public void getReceivedAmount_invalidCryptocurrency() {
        ReceivedAmount receivedAmount = wallet.getReceivedAmount(ADDRESS_EMPTY, CryptoCurrency.BTC.getCode());
        assertEquals(ReceivedAmount.ZERO, receivedAmount);
    }

    @Test
    public void getReceivedAmount_nullClientResponse() {
        when(client.getReceivedByAddress(eq(ADDRESS_EMPTY), anyInt())).thenReturn(null);
        ReceivedAmount receivedAmount = wallet.getReceivedAmount(ADDRESS_EMPTY, CryptoCurrency.L_BTC.getCode());
        assertEquals(ReceivedAmount.ZERO, receivedAmount);
    }

    @Test
    public void getReceivedAmount_amountNotReceived() {
        when(client.getReceivedByAddress(eq(ADDRESS_EMPTY), anyInt())).thenReturn(BigDecimal.ZERO);
        ReceivedAmount receivedAmount = wallet.getReceivedAmount(ADDRESS_EMPTY, CryptoCurrency.L_BTC.getCode());
        assertEquals(ReceivedAmount.ZERO, receivedAmount);
    }
    @Test
    public void getReceivedAmount_amountWithTwoConfirmations() {
        when(client.getReceivedByAddress(eq(ADDRESS_WITH_MORE_TXS), anyInt())).thenReturn(BigDecimal.ZERO);
        lenient().when(client.getReceivedByAddress(ADDRESS_WITH_MORE_TXS, 1)).thenReturn(BigDecimal.TEN);
        when(client.getReceivedByAddress(ADDRESS_WITH_MORE_TXS, 2)).thenReturn(BigDecimal.ONE);

        ReceivedAmount receivedAmount = wallet.getReceivedAmount(ADDRESS_WITH_MORE_TXS, CryptoCurrency.L_BTC.getCode());
        assertEquals(BigDecimal.ONE, receivedAmount.getTotalAmountReceived());
        assertEquals(2, receivedAmount.getConfirmations());
        assertNull(receivedAmount.getTransactionHashes());
    }
    @Test
    public void getReceivedAmount_amountWithOneConfirmations() {
        when(client.getReceivedByAddress(eq(ADDRESS_WITH_COINS), anyInt())).thenReturn(BigDecimal.ZERO);
        when(client.getReceivedByAddress(ADDRESS_WITH_COINS, 1)).thenReturn(BigDecimal.TEN);

        ReceivedAmount receivedAmount = wallet.getReceivedAmount(ADDRESS_WITH_COINS, CryptoCurrency.L_BTC.getCode());
        assertEquals(BigDecimal.TEN, receivedAmount.getTotalAmountReceived());
        assertEquals(1, receivedAmount.getConfirmations());
        assertNull(receivedAmount.getTransactionHashes());
    }

}

