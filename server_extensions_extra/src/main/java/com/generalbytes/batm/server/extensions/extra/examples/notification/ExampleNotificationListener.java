package com.generalbytes.batm.server.extensions.extra.examples.notification;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IIdentity;
import com.generalbytes.batm.server.extensions.IIdentityPiece;
import com.generalbytes.batm.server.extensions.INotificationListener;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ExampleNotificationListener implements INotificationListener {

    private final IExtensionContext ctx;

    public ExampleNotificationListener(IExtensionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void transactionCashLimitReached(String terminalSerialNumber,
                                            BigDecimal cashAmount,
                                            String cashCurrency,
                                            String identityPublicId,
                                            String limitName,
                                            BigDecimal resultingLimit,
                                            Map<String, BigDecimal> limitsReached) {
        switch (limitName) {
            case "LIMIT_DAY":
                getIdentityEmail(identityPublicId).ifPresent(this::sendMailDay);
                break;
            case "LIMIT_TRANSACTION":
                getIdentityEmail(identityPublicId).ifPresent(this::sendMailTransaction);
                break;
        }
    }

    private void sendMailTransaction(String email) {
        ctx.sendMailAsync("operator@example.com",
            email,
            "Bitcoin ATM Transaction Limit Reached",
            "You have reached your transaction limit, please make a transaction for a smaller amount",
            null);
    }

    private void sendMailDay(String email) {
        ctx.sendMailAsync("operator@example.com",
            email,
            "Bitcoin ATM Daily Limit Reached",
            "You have reached your daily limit, please come again later",
            null);
    }

    private Optional<String> getIdentityEmail(String identityPublicId) {
        return Optional.ofNullable(identityPublicId)
            .map(ctx::findIdentityByIdentityId)
            .map(IIdentity::getIdentityPieces)
            .flatMap(pieces -> pieces.stream()
                .map(IIdentityPiece::getEmailAddress)
                .filter(Objects::nonNull)
                .findAny());
    }

}
