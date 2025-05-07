package com.generalbytes.batm.server.extensions.extra.nano.rpc;

import com.generalbytes.batm.server.extensions.extra.nano.rpc.dto.Block;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NanoRpcClientTest {

    private static final String TEST_ADDRESS = "testAddress";
    private static final MediaType JSON_CONTENT_TYPE = MediaType.parse("application/json");

    private static final OkHttpClient httpClient = mock(OkHttpClient.class);

    private NanoRpcClient nanoRpcClient;

    @BeforeEach
    void setUp() throws MalformedURLException {
        reset(httpClient);

        try (MockedConstruction<OkHttpClient.Builder> okHttpClientBuilderMockedConstruction = mockConstruction(OkHttpClient.Builder.class,
                (mock, context) -> {
                    when(mock.callTimeout(anyLong(), any())).thenReturn(mock);
                    when(mock.build()).thenReturn(httpClient);
                })) {
            URL url = new URL("https://rpc.nano.to/"); // Any url will do
            nanoRpcClient = new NanoRpcClient(url);

            if (!okHttpClientBuilderMockedConstruction.constructed().isEmpty()) {
                OkHttpClient.Builder builder = okHttpClientBuilderMockedConstruction.constructed().get(0);
                verify(builder).callTimeout(10, TimeUnit.SECONDS);
            }
        }
    }

    @Test
    void testGetTransactionHistory_responseNotObject() throws IOException {
        mockResponse("[]");

        RpcException exception = assertThrows(RpcException.class,
                () -> nanoRpcClient.getTransactionHistory(TEST_ADDRESS));

        assertEquals("Response is not a JSON object.", exception.getMessage());
    }

    @Test
    void testGetTransactionHistory_responseHasError() throws IOException {
        mockResponse("{\"error\": \"Test Error\"}");

        RpcException exception = assertThrows(RpcException.class,
                () -> nanoRpcClient.getTransactionHistory(TEST_ADDRESS));

        assertEquals("Test Error", exception.getMessage());
    }

    @Test
    void testGetTransactionHistory_responseHasError_accountNotFound() throws IOException, RpcException {
        mockResponse("{\"error\": \"Account not found\"}");

        List<Block> transactionHistory = nanoRpcClient.getTransactionHistory(TEST_ADDRESS);

        assertNotNull(transactionHistory);
        assertEquals(0, transactionHistory.size());
    }

    @Test
    void testGetTransactionHistory_emptyHistory() throws IOException, RpcException {
        mockResponse("{\"history\": []}");

        List<Block> transactionHistory = nanoRpcClient.getTransactionHistory(TEST_ADDRESS);

        assertNotNull(transactionHistory);
        assertEquals(0, transactionHistory.size());
    }

    @Test
    void testGetTransactionHistory() throws IOException, RpcException {
        mockResponse("{\"history\": [" +
                "{\"type\": \"receive\", \"account\": \"testAccount1\", \"amount\": \"1000\"}," +
                "{\"type\": \"send\", \"account\": \"testAccount2\", \"amount\": \"2000\"}" +
                "]}");

        List<Block> transactionHistory = nanoRpcClient.getTransactionHistory(TEST_ADDRESS);

        assertNotNull(transactionHistory);
        assertEquals(2, transactionHistory.size());
        Block block1 = transactionHistory.get(0);
        assertBlock(block1, "receive", "testAccount1", "1000");
        Block block2 = transactionHistory.get(1);
        assertBlock(block2, "send", "testAccount2", "2000");
    }

    private void assertBlock(Block block, String receive, String account, String number) {
        assertEquals(receive, block.type());
        assertEquals(account, block.account());
        assertEquals(number, block.amount().toString());
    }

    private void mockResponse(String responseBodyJson) throws IOException {
        Call call = mock(Call.class);
        Response response = mock(Response.class);
        when(response.body()).thenReturn(ResponseBody.create(responseBodyJson, JSON_CONTENT_TYPE));
        when(httpClient.newCall(any())).thenReturn(call);
        when(call.execute()).thenReturn(response);
    }

}