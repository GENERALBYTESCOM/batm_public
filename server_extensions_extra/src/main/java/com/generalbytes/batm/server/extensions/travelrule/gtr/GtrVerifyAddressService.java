package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressRequest;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Service for address verification with Global Travel Rule (GTR).
 */
@Slf4j
@AllArgsConstructor
public class GtrVerifyAddressService {

    private final GtrApiWrapper api;

    /**
     * Verify address with GTR. The method sequentially verifies the address on all networks of a given cryptocurrency
     * until it finds the first successful verification (if any).
     *
     * @param request       Request object for calling GTR api containing the necessary data.
     * @param cryptoNetwork Cryptocurrency and its supported networks.
     * @return Object containing data about the result of address verification.
     */
    public GtrVerifyAddressResponse verifyAddress(GtrCredentials credentials,
                                                  GtrVerifyAddressRequest request,
                                                  GtrCryptoNetwork cryptoNetwork
    ) {
        GtrVerifyAddressResponse response;

        int index = 0;
        String[] networks = cryptoNetwork.getNetworks();
        do {
            request.setNetwork(networks[index]);

            response = api.verifyAddress(credentials, request);
            if (response.isSuccess()) {
                logSuccessAddressVerification(request);
                return response;
            }

            index++;
        } while (index < networks.length);

        logFailedAddressVerification(request, response);
        return response;
    }

    private void logSuccessAddressVerification(GtrVerifyAddressRequest request) {
        if (StringUtils.isBlank(request.getTag())) {
            log.info("GTR, request ID '{}': address '{}' has been verified, cryptocurrency: {}, network {}, target VASP: {}",
                    request.getRequestId(), request.getAddress(), request.getTicker(), request.getNetwork(), request.getTargetVaspCode());
        } else {
            log.info("GTR, request ID '{}': address '{}' with tag '{}' has been verified, cryptocurrency: {}, network {}, target VASP: {}",
                    request.getRequestId(), request.getAddress(), request.getTag(), request.getTicker(),
                    request.getNetwork(), request.getTargetVaspCode());
        }
    }

    private void logFailedAddressVerification(GtrVerifyAddressRequest request, GtrVerifyAddressResponse response) {
        if (StringUtils.isBlank(request.getTag())) {
            log.info("GTR, request ID '{}': address '{}' was not verified, cryptocurrency: {}, target VASP: {},"
                            + " response code: {}, response message: {}",
                    request.getRequestId(), request.getAddress(), request.getTicker(), request.getTargetVaspCode(),
                    response.getStatusCode(), response.getMessage());
        } else {
            log.info("GTR, request ID '{}': address '{}' with tag '{}' was not verified, cryptocurrency: {}, target VASP: {},"
                            + " response code: {}, response message: {}",
                    request.getRequestId(), request.getAddress(), request.getTag(), request.getTicker(), request.getTargetVaspCode(),
                    response.getStatusCode(), response.getMessage());
        }
    }

}
