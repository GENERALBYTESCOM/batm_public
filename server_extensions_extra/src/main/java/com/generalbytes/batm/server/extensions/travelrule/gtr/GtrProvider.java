package com.generalbytes.batm.server.extensions.travelrule.gtr;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.travelrule.CryptoWalletType;
import com.generalbytes.batm.server.extensions.travelrule.IIdentityWalletEvaluationRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleProvider;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferData;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferInfo;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferListener;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferResolvedEvent;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleTransferUpdateRequest;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleVasp;
import com.generalbytes.batm.server.extensions.travelrule.ITravelRuleWalletInfo;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyAddressResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.mapper.GtrProviderMapper;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrConfiguration;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrCredentials;
import com.generalbytes.batm.server.extensions.travelrule.gtr.dto.GtrVerifyPiiResponse;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrTransferHandler;
import com.generalbytes.batm.server.extensions.travelrule.gtr.handler.GtrVerifyPiiWebhookHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * Global Travel Rule (GTR) provider.
 */
@Slf4j
@AllArgsConstructor
public class GtrProvider implements ITravelRuleProvider {

    public static final String NAME = "Global Travel Rule Provider";

    @Getter
    private GtrCredentials credentials;
    private final GtrConfiguration configuration;
    private final GtrService gtrService;
    private final GtrTransferHandler gtrTransferHandler;
    /**
     * Can be {@code null}. See {@link GtrProviderFactory#GtrProviderFactory}.
     */
    private final GtrVerifyPiiWebhookHandler verifyPiiWebhookHandler;
    private final GtrAuthService authService;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ITravelRuleWalletInfo getWalletInfo(IIdentityWalletEvaluationRequest walletEvaluationRequest) {
        if (StringUtils.isBlank(walletEvaluationRequest.getDidOfVaspHostingCustodialWallet())) {
            return createUnknownWalletInfo();
        }

        GtrVerifyAddressResponse response = gtrService.getWalletInfo(credentials, walletEvaluationRequest);
        if (response.isSuccess()) {
            return createCustodialWalletInfo(walletEvaluationRequest);
        }

        return createUnknownWalletInfo();
    }

    private ITravelRuleWalletInfo createUnknownWalletInfo() {
        return GtrProviderMapper.toITravelRuleWalletInfo(CryptoWalletType.UNKNOWN, null);
    }

    private ITravelRuleWalletInfo createCustodialWalletInfo(IIdentityWalletEvaluationRequest walletEvaluationRequest) {
        return GtrProviderMapper.toITravelRuleWalletInfo(
                CryptoWalletType.CUSTODIAL, walletEvaluationRequest.getDidOfVaspHostingCustodialWallet()
        );
    }

    @Override
    public boolean verifyCustomerDeclaredCustodialWallet() {
        return true;
    }

    @Override
    public List<ITravelRuleVasp> getAllVasps() {
        return gtrService.getAllVasps(credentials).stream()
                .map(GtrProviderMapper::toITravelRuleVasp)
                .toList();
    }

    @Override
    public ITravelRuleTransferInfo createTransfer(ITravelRuleTransferData outgoingTransferData) {
        GtrVerifyPiiResponse verifyPiiResponse = gtrService.createTransfer(credentials, outgoingTransferData);
        return GtrProviderMapper.toITravelRuleTransferInfo(verifyPiiResponse);
    }

    @Override
    public boolean registerTransferListener(ITravelRuleTransferListener transferListener) {
        gtrTransferHandler.registerTransferListener(transferListener);
        return true;
    }

    @Override
    public boolean unregisterTransferListener() {
        log.debug("Transfer listener for GTR cannot be unregistered.");
        return true;
    }

    @Override
    public ITravelRuleTransferInfo updateTransfer(ITravelRuleTransferUpdateRequest updateRequest) {
        gtrService.notifyTxId(credentials, updateRequest);
        return updateRequest::getId;
    }

    @Override
    public void notifyProviderConfigurationChanged() {
        // Handled by the GtrProviderFactory and GtrProvider#updateCredentials
    }

    @Override
    public boolean testProviderConfiguration() {
        log.info("A configuration test was requested for {}, client ID: {}", NAME, credentials.getAccessKey());

        return gtrService.testProviderCredentials(credentials);
    }

    @Override
    public boolean onTransferResolved(ITravelRuleTransferResolvedEvent event) {
        if (!configuration.isWebhooksEnabled()) {
            log.warn("To use the onTransferResolved method, enable GTR webhooks.");
            return false;
        }

        return verifyPiiWebhookHandler.onTransferResolved(event);
    }

    /**
     * Updates the current credentials if they differ from the provided ones.
     *
     * <p>If the new credentials are identical to the current credentials, no action is taken.
     * Otherwise, the method invalidates the existing access token, and then sets the new credentials.</p>
     *
     * @param credentials The new credentials.
     */
    void updateCredentials(GtrCredentials credentials) {
        if (credentialsMatch(this.credentials, credentials)) {
            return;
        }

        log.debug("GTR credentials changed for VASP '{}'.", this.credentials.getVaspCode());

        // Invalidate the current access token using the old credentials
        authService.removeAccessToken(this.credentials);

        // Start using the new credentials
        this.credentials = credentials;
    }

    private boolean credentialsMatch(GtrCredentials currentCredentials, GtrCredentials newCredentials) {
        return Objects.equals(currentCredentials.getAccessKey(), newCredentials.getAccessKey())
                && Objects.equals(currentCredentials.getSignedSecretKey(), newCredentials.getSignedSecretKey())
                && Objects.equals(currentCredentials.getCurvePublicKey(), newCredentials.getCurvePublicKey())
                && Objects.equals(currentCredentials.getCurvePrivateKey(), newCredentials.getCurvePrivateKey());
    }

}
