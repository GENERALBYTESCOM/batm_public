package com.generalbytes.batm.server.extensions.travelrule.gtr.mapper;

import com.generalbytes.batm.server.extensions.travelrule.CryptoWalletType;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleVasp;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleWalletInfo;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrNotifyTxIdRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVaspBasicInfo;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Global Travel Rule (GTR) mapper for mapping request and response objects to/from GTR API.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GtrProviderMapper {

    /**
     * Maps {@link GtrVaspBasicInfo} to {@link ITravelRuleVasp} object.
     *
     * @param gtrVaspBasicInfo {@link GtrVaspBasicInfo}
     * @return Object of {@link ITravelRuleVasp}.
     */
    public static ITravelRuleVasp toITravelRuleVasp(GtrVaspBasicInfo gtrVaspBasicInfo) {
        return new ITravelRuleVasp() {
            @Override
            public String getDid() {
                return gtrVaspBasicInfo.getVaspCode();
            }

            @Override
            public String getName() {
                return gtrVaspBasicInfo.getVaspName();
            }
        };
    }

    /**
     * Maps values to {@link GtrVerifyAddressRequest} object.
     *
     * @param requestId               Request ID.
     * @param initiatorVaspPublicKey  Public key of initiator VASP.
     * @param targetVaspPublicKey     Public key of target VASP.
     * @param targetVaspDid           Target (beneficiary) VASP DID.
     * @param cryptocurrency          Cryptocurrency.
     * @param cryptoAddress           Destination crypto address.
     * @return Object of {@link GtrVerifyAddressRequest}.
     */
    public static GtrVerifyAddressRequest toGtrVerifyAddressRequest(String requestId,
                                                                    String initiatorVaspPublicKey,
                                                                    String targetVaspPublicKey,
                                                                    String targetVaspDid,
                                                                    String cryptocurrency,
                                                                    String cryptoAddress
    ) {
        AddressWithTag addressWithTag = getAddressWithTag(cryptoAddress);

        GtrVerifyAddressRequest request = new GtrVerifyAddressRequest();
        request.setRequestId(requestId);
        request.setAddress(addressWithTag.address());
        request.setTag(addressWithTag.tag());
        request.setInitiatorPublicKey(initiatorVaspPublicKey);
        request.setTargetVaspCode(targetVaspDid);
        request.setTargetVaspPublicKey(targetVaspPublicKey);
        request.setTicker(cryptocurrency);

        return request;
    }

    private static AddressWithTag getAddressWithTag(String address) {
        String[] addressAndTag = address.split(":");
        if (addressAndTag.length == 2) {
            return new AddressWithTag(addressAndTag[0], addressAndTag[1]);
        }

        return new AddressWithTag(addressAndTag[0], null);
    }

    private record AddressWithTag(String address, String tag) {

    }

    /**
     * Maps {@link CryptoWalletType} and VASP DID to {@link ITravelRuleWalletInfo}.
     *
     * @param walletType {@link CryptoWalletType}
     * @param vaspDid    VASP DID.
     * @return {@link ITravelRuleWalletInfo}
     */
    public static ITravelRuleWalletInfo toITravelRuleWalletInfo(CryptoWalletType walletType, String vaspDid) {
        return new ITravelRuleWalletInfo() {
            @Override
            public CryptoWalletType getCryptoWalletType() {
                return walletType;
            }

            @Override
            public String getOwnerVaspDid() {
                return vaspDid;
            }
        };
    }

    /**
     * Map {@link GtrVerifyPiiResponse} to {@link ITravelRuleTransferInfo}.
     *
     * @param verifyPiiResponse {@link GtrVerifyPiiResponse}
     * @return {@link ITravelRuleTransferInfo} with request ID.
     */
    public static ITravelRuleTransferInfo toITravelRuleTransferInfo(GtrVerifyPiiResponse verifyPiiResponse) {
        return verifyPiiResponse::getRequestId;
    }

    /**
     * Map {@link ITravelRuleTransferUpdateRequest} to {@link GtrNotifyTxIdRequest}.
     *
     * @param transferUpdateRequest {@link ITravelRuleTransferUpdateRequest}
     * @return {@link GtrNotifyTxIdRequest} with on-chain transaction hash.
     */
    public static GtrNotifyTxIdRequest toGtrNotifyTxIdRequest(ITravelRuleTransferUpdateRequest transferUpdateRequest) {
        GtrNotifyTxIdRequest notifyTxIdRequest = new GtrNotifyTxIdRequest();
        notifyTxIdRequest.setRequestId(transferUpdateRequest.getId());
        notifyTxIdRequest.setTxId(transferUpdateRequest.getTransactionHash());

        return notifyTxIdRequest;
    }

}
