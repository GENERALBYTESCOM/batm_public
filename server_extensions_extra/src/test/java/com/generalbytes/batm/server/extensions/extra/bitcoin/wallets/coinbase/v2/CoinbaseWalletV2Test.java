package com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2;

import com.generalbytes.batm.server.extensions.extra.bitcoin.coinbase.api.CoinbaseV2ApiWrapper;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBAddress;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBError;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBPaginatedResponse;
import com.generalbytes.batm.server.extensions.extra.bitcoin.wallets.coinbase.v2.dto.CBPagination;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CoinbaseWalletV2Test {

    @Mock
    private CoinbaseV2ApiWrapper apiWrapper;
    private CoinbaseWalletV2 wallet;

    @BeforeEach
    void setUp() {
        wallet = new CoinbaseWalletV2(apiWrapper, "testAccount");
    }

    @Test
    void testPaginate_errorsInResponse() {
        Exception exception = assertThrows(IllegalStateException.class, () -> wallet.paginate(startingAfter -> {
            CBPaginatedResponse<CBAddress> response = new CBPaginatedResponse<>();
            response.setErrors(List.of(createError()));
            return response;
        }));

        assertEquals("id = errorId, message = errorMessage", exception.getMessage());
    }

    private static Object[] provideBlankResponseData() {
        return new Object[]{
                null,
                List.of()
        };
    }

    @ParameterizedTest
    @MethodSource("provideBlankResponseData")
    void testPaginate_noDataInResponse(List<CBAddress> data) {
        List<CBAddress> result = wallet.paginate(startingAfter -> {
            CBPaginatedResponse<CBAddress> response = new CBPaginatedResponse<>();
            response.setData(data);
            return response;
        });

        assertTrue(result.isEmpty());
    }

    @Test
    void testPaginate_noPagination() {
        List<CBAddress> result = wallet.paginate(startingAfter -> createPaginatedResponse(null, List.of(createAddress("address1"))));

        assertEquals(1, result.size());
        assertEquals("address1", result.get(0).getId());
    }

    private static Object[][] provideNextUriOrEndingBefore() {
        return new Object[][]{
                // nextUri, endingBefore
                {null, "address3"},
                {"address3", null}
        };
    }

    @ParameterizedTest
    @MethodSource("provideNextUriOrEndingBefore")
    void testPaginate(String nextUri, String endingBefore) {
        List<CBAddress> result = wallet.paginate(startingAfter -> {
            if (startingAfter == null) {
                return createPaginatedResponse(createPagination(nextUri, endingBefore), List.of(
                        createAddress("address1"),
                        createAddress("address2"),
                        createAddress("address3")
                ));
            } else if ("address3".equals(startingAfter)) {
                return createPaginatedResponse(createPagination(null, null), List.of(
                        createAddress("address4"),
                        createAddress("address5")
                ));
            } else {
                return new CBPaginatedResponse<>(); // Simulate no data for other cases
            }
        });

        assertEquals(5, result.size());
        assertEquals("address1", result.get(0).getId());
        assertEquals("address2", result.get(1).getId());
        assertEquals("address3", result.get(2).getId());
        assertEquals("address4", result.get(3).getId());
        assertEquals("address5", result.get(4).getId());
    }

    private CBPaginatedResponse<CBAddress> createPaginatedResponse(CBPagination pagination, List<CBAddress> addresses) {
        CBPaginatedResponse<CBAddress> response = new CBPaginatedResponse<>();
        response.setData(addresses);
        response.setPagination(pagination);
        return response;
    }

    private CBPagination createPagination(String nextUri, String endingBefore) {
        CBPagination pagination = new CBPagination();
        pagination.setNext_uri(nextUri);
        pagination.setEnding_before(endingBefore);
        return pagination;
    }

    private CBAddress createAddress(String id) {
        CBAddress cbAddress = new CBAddress();
        cbAddress.setId(id);
        return cbAddress;
    }

    private CBError createError() {
        CBError error = new CBError();
        error.setId("errorId");
        error.setMessage("errorMessage");
        return error;
    }

}