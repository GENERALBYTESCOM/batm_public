package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2;

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.CoinbaseV2ApiWrapper;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAccount;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBBalance;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCreateAddressRequest;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCreateAddressResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBCurrency;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBNetwork;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBPaginatedResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBTransaction;
import com.generalbytes.batm.server.extensions.payment.ReceivedAmount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoinbaseWalletV2WithUniqueAddressesTest {

    private static final String TEST_ACCOUNT_NAME = "testAccount";
    private static final String TEST_ACCOUNT_ID = "accountId";

    @Mock
    private CoinbaseV2ApiWrapper apiWrapper;
    private CoinbaseWalletV2WithUniqueAddresses wallet;

    @BeforeEach
    void setUp() {
        wallet = new CoinbaseWalletV2WithUniqueAddresses(apiWrapper, TEST_ACCOUNT_NAME);
    }

    @Test
    void testGenerateNewDepositCryptoAddress_unsupportedCryptoCurrency() {
        String result = wallet.generateNewDepositCryptoAddress("unsupportedCryptoCurrency", "label");

        assertNull(result);
    }

    private static Object[][] provideInvalidCreateAddressResponses() {
        return new Object[][]{
            {null},
            {new CBCreateAddressResponse()},
        };
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCreateAddressResponses")
    void testGenerateNewDepositCryptoAddress_invalidResponse() {
        CBPaginatedResponse<CBAccount> accountsResponse = new CBPaginatedResponse<>();
        accountsResponse.setData(List.of(createAccount()));

        when(apiWrapper.getAccounts(any(), anyLong(), anyInt(), any())).thenReturn(accountsResponse);
        when(apiWrapper.createAddress(any(), anyLong(), any(), any())).thenReturn(null);

        String result = wallet.generateNewDepositCryptoAddress("BTC", "label");

        assertNull(result);
    }

    @Test
    void testGenerateNewDepositCryptoAddress_invalidNetwork() {
        CBCreateAddressResponse createAddressResponse = new CBCreateAddressResponse();
        CBAddress address = new CBAddress();
        address.setNetwork("someOtherNetwork");
        createAddressResponse.setData(address);

        CBPaginatedResponse<CBAccount> accountsResponse = new CBPaginatedResponse<>();
        accountsResponse.setData(List.of(createAccount()));

        when(apiWrapper.getAccounts(any(), anyLong(), anyInt(), any())).thenReturn(accountsResponse);
        when(apiWrapper.createAddress(any(), anyLong(), any(), any())).thenReturn(createAddressResponse);

        String result = wallet.generateNewDepositCryptoAddress("BTC", "label");

        assertNull(result);
    }

    @Test
    void testGenerateNewDepositCryptoAddress() {
        CBCreateAddressResponse createAddressResponse = new CBCreateAddressResponse();
        CBAddress address = new CBAddress();
        address.setNetwork("bitcoin");
        address.setAddress("address");
        createAddressResponse.setData(address);

        CBPaginatedResponse<CBAccount> accountsResponse = new CBPaginatedResponse<>();
        accountsResponse.setData(List.of(createAccount()));

        when(apiWrapper.getAccounts(any(), anyLong(), anyInt(), any())).thenReturn(accountsResponse);
        when(apiWrapper.createAddress(any(), anyLong(), any(), any())).thenReturn(createAddressResponse);

        String result = wallet.generateNewDepositCryptoAddress("BTC", "label");

        assertEquals("address", result);
        ArgumentCaptor<CBCreateAddressRequest> requestCaptor = ArgumentCaptor.forClass(CBCreateAddressRequest.class);
        verify(apiWrapper).createAddress(eq(CoinbaseWalletV2.API_VERSION), anyLong(), eq(TEST_ACCOUNT_ID), requestCaptor.capture());
        CBCreateAddressRequest request = requestCaptor.getValue();
        assertEquals("label", request.name);
    }

    @Test
    void testGetReceivedAmount_unsupportedCryptoCurrency() {
        ReceivedAmount result = wallet.getReceivedAmount("address", "unsupportedCryptoCurrency");

        assertEquals(BigDecimal.ZERO, result.getTotalAmountReceived());
        assertEquals(0, result.getConfirmations());
        assertNull(result.getTransactionHashes());
    }

    private static Object[][] provideInvalidAddressesResponse() {
        return new Object[][]{
            {null},
            {List.of()},
            {List.of(createAddress("someOtherAddress", "id"))},
        };
    }

    @ParameterizedTest
    @MethodSource("provideInvalidAddressesResponse")
    void testGetReceivedAmount_noAddress(List<CBAddress> addresses) {
        CBPaginatedResponse<CBAddress> addressesResponse = new CBPaginatedResponse<>();
        addressesResponse.setData(addresses);
        CBPaginatedResponse<CBAccount> accountsResponse = new CBPaginatedResponse<>();
        accountsResponse.setData(List.of(createAccount()));

        when(apiWrapper.getAddresses(any(), anyLong(), any(), anyInt(), any())).thenReturn(addressesResponse);
        when(apiWrapper.getAccounts(any(), anyLong(), anyInt(), any())).thenReturn(accountsResponse);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> wallet.getReceivedAmount("address", "BTC"));

        assertEquals("Address 'address' not found", exception.getMessage());
    }

    @Test
    void testGetReceivedAmount_noTransactions() {
        CBAddress address = createAddress("address", "addressId");
        CBPaginatedResponse<CBAddress> addressesResponse = new CBPaginatedResponse<>();
        addressesResponse.setData(List.of(address));
        CBPaginatedResponse<CBAccount> accountsResponse = new CBPaginatedResponse<>();
        accountsResponse.setData(List.of(createAccount()));
        CBPaginatedResponse<CBTransaction> transactionsResponse = new CBPaginatedResponse<>();
        transactionsResponse.setData(List.of());

        when(apiWrapper.getAddresses(any(), anyLong(), any(), anyInt(), any())).thenReturn(addressesResponse);
        when(apiWrapper.getAccounts(any(), anyLong(), anyInt(), any())).thenReturn(accountsResponse);
        when(apiWrapper.getAddressTransactions(any(), anyLong(), any(), any(), anyInt(), any())).thenReturn(transactionsResponse);

        ReceivedAmount result = wallet.getReceivedAmount("address", "BTC");

        assertEquals(BigDecimal.ZERO, result.getTotalAmountReceived());
        assertEquals(0, result.getConfirmations());
        assertNotNull(result.getTransactionHashes());
        assertTrue(result.getTransactionHashes().isEmpty());
    }

    @Test
    void testGetReceivedAmount() {
        CBAddress address = createAddress("address", "addressId");
        CBPaginatedResponse<CBAddress> addressesResponse = new CBPaginatedResponse<>();
        addressesResponse.setData(List.of(address));
        CBPaginatedResponse<CBAccount> accountsResponse = new CBPaginatedResponse<>();
        accountsResponse.setData(List.of(createAccount()));
        CBPaginatedResponse<CBTransaction> transactionsResponse = new CBPaginatedResponse<>();
        transactionsResponse.setData(List.of(
            createTransaction("send", "completed", createBalance(BigDecimal.TEN, "BTC"), "hash1"),
            createTransaction("send", "pending", createBalance(BigDecimal.ONE, "BTC"), "hash2"),
            createTransaction("send", "completed", createBalance(BigDecimal.ONE, "BTC"), " "),
            createTransaction("send", "completed", createBalance(BigDecimal.ONE, "BTC"), null),
            // Wrong cryptocurrency
            createTransaction("send", "completed", createBalance(BigDecimal.ONE, "ETH"), null),
            // Null amount
            createTransaction("send", "completed", null, null),
            // Invalid status
            createTransaction("send", "failed", createBalance(BigDecimal.ONE, "BTC"), null),
            // Wrong type
            createTransaction("buy", "completed", createBalance(BigDecimal.TEN, "BTC"), "hash3")
        ));

        when(apiWrapper.getAddresses(any(), anyLong(), any(), anyInt(), any())).thenReturn(addressesResponse);
        when(apiWrapper.getAccounts(any(), anyLong(), anyInt(), any())).thenReturn(accountsResponse);
        when(apiWrapper.getAddressTransactions(any(), anyLong(), any(), any(), anyInt(), any())).thenReturn(transactionsResponse);

        ReceivedAmount result = wallet.getReceivedAmount("address", "BTC");

        assertEquals(BigDecimal.valueOf(13), result.getTotalAmountReceived());
        assertEquals(0, result.getConfirmations());
        assertNotNull(result.getTransactionHashes());
        assertEquals(2, result.getTransactionHashes().size());
        assertTrue(result.getTransactionHashes().contains("hash1"));
        assertTrue(result.getTransactionHashes().contains("hash2"));
    }

    @Test
    void testGetReceivedAmount_allTransactionsCompleted() {
        CBAddress address = createAddress("address", "addressId");
        CBPaginatedResponse<CBAddress> addressesResponse = new CBPaginatedResponse<>();
        addressesResponse.setData(List.of(address));
        CBPaginatedResponse<CBAccount> accountsResponse = new CBPaginatedResponse<>();
        accountsResponse.setData(List.of(createAccount()));
        CBPaginatedResponse<CBTransaction> transactionsResponse = new CBPaginatedResponse<>();
        transactionsResponse.setData(List.of(
            createTransaction("send", "completed", createBalance(BigDecimal.TEN, "BTC"), "hash1"),
            createTransaction("send", "completed", createBalance(BigDecimal.ONE, "BTC"), null)
        ));

        when(apiWrapper.getAddresses(any(), anyLong(), any(), anyInt(), any())).thenReturn(addressesResponse);
        when(apiWrapper.getAccounts(any(), anyLong(), anyInt(), any())).thenReturn(accountsResponse);
        when(apiWrapper.getAddressTransactions(any(), anyLong(), any(), any(), anyInt(), any())).thenReturn(transactionsResponse);

        ReceivedAmount result = wallet.getReceivedAmount("address", "BTC");

        assertEquals(BigDecimal.valueOf(11), result.getTotalAmountReceived());
        assertEquals(999, result.getConfirmations());
        assertNotNull(result.getTransactionHashes());
        assertEquals(1, result.getTransactionHashes().size());
        assertTrue(result.getTransactionHashes().contains("hash1"));
    }

    private static CBAddress createAddress(String address, String id) {
        CBAddress cbAddress = new CBAddress();
        cbAddress.setId(id);
        cbAddress.setAddress(address);
        return cbAddress;
    }

    private static CBAccount createAccount() {
        CBCurrency currency = new CBCurrency();
        currency.setCode("BTC");

        CBAccount account = new CBAccount();
        account.setId(TEST_ACCOUNT_ID);
        account.setName(TEST_ACCOUNT_NAME);
        account.setCurrency(currency);
        return account;
    }

    private static CBTransaction createTransaction(String type, String status, CBBalance amount, String hash) {
        CBNetwork network = new CBNetwork();
        network.setHash(hash);

        CBTransaction transaction = new CBTransaction();
        transaction.setId("transactionId");
        transaction.setType(type);
        transaction.setStatus(status);
        transaction.setAmount(amount);
        transaction.setNetwork(network);
        return transaction;
    }

    private static CBBalance createBalance(BigDecimal amount, String cryptocurrency) {
        CBBalance balance = new CBBalance();
        balance.setAmount(amount);
        balance.setCurrency(cryptocurrency);
        return balance;
    }

}