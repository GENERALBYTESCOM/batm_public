package com.generalbytes.batm.server.extensions.travelrule.gtr.mapper;

import com.generalbytes.batm.server.extensions.travelrule.CryptoWalletType;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleVasp;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleWalletInfo;
import com.generalbytes.batm.server.extensions.travelrule.gtr.GtrCryptoNetwork;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrNotifyTxIdRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVaspBasicInfo;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GtrProviderMapperTest {

    @Test
    void testToITravelRuleVasp() {
        GtrVaspBasicInfo basicInfo = createGtrVaspBasicInfo();

        ITravelRuleVasp vasp = GtrProviderMapper.toITravelRuleVasp(basicInfo);

        assertEquals("vasp code", vasp.getDid());
        assertEquals("vasp name", vasp.getName());
    }

    private GtrVaspBasicInfo createGtrVaspBasicInfo() {
        GtrVaspBasicInfo basicInfo = mock(GtrVaspBasicInfo.class);
        when(basicInfo.getVaspCode()).thenReturn("vasp code");
        when(basicInfo.getVaspName()).thenReturn("vasp name");

        return basicInfo;
    }

    private static Stream<Arguments> testToGtrVerifyAddressRequest_arguments() {
        return Stream.of(
                arguments("address", null),
                arguments("address:tag", "tag"),
                arguments("address:", null)
        );
    }

    @ParameterizedTest
    @MethodSource("testToGtrVerifyAddressRequest_arguments")
    void testToGtrVerifyAddressRequest(String cryptoAddress, String expectedTag) {
        GtrVerifyAddressRequest request = GtrProviderMapper.toGtrVerifyAddressRequest(
                "request_id", "curve_public_key", "target_vasp_public_key", "target_vasp_code", "BTC", cryptoAddress
        );

        assertEquals("request_id", request.getRequestId());
        assertEquals("address", request.getAddress());
        assertEquals(expectedTag, request.getTag());
        assertEquals("curve_public_key", request.getInitiatorPublicKey());
        assertEquals("target_vasp_code", request.getTargetVaspCode());
        assertEquals("target_vasp_public_key", request.getTargetVaspPublicKey());
        assertEquals(GtrCryptoNetwork.BTC.name(), request.getTicker());
        assertNull(request.getNetwork());
    }

    @ParameterizedTest
    @EnumSource(CryptoWalletType.class)
    void testToITravelRuleWalletInfo(CryptoWalletType walletType) {
        ITravelRuleWalletInfo walletInfo = GtrProviderMapper.toITravelRuleWalletInfo(walletType, "vasp_did");
        assertEquals(walletType, walletInfo.getCryptoWalletType());
        assertEquals("vasp_did", walletInfo.getOwnerVaspDid());
    }

    @Test
    void testToITravelRuleTransferInfo() {
        GtrVerifyPiiResponse verifyPiiResponse = mock(GtrVerifyPiiResponse.class);
        when(verifyPiiResponse.getRequestId()).thenReturn("request_id");

        ITravelRuleTransferInfo transferInfo = GtrProviderMapper.toITravelRuleTransferInfo(verifyPiiResponse);

        assertEquals("request_id", transferInfo.getId());
    }

    @Test
    void testToGtrNotifyTxIdRequest() {
        ITravelRuleTransferUpdateRequest transferUpdateRequest = mock(ITravelRuleTransferUpdateRequest.class);
        when(transferUpdateRequest.getId()).thenReturn("request_id");
        when(transferUpdateRequest.getTransactionHash()).thenReturn("tx_hash");

        GtrNotifyTxIdRequest request = GtrProviderMapper.toGtrNotifyTxIdRequest(transferUpdateRequest);

        assertEquals("request_id", request.getRequestId());
        assertEquals("tx_hash", request.getTxId());
    }

}