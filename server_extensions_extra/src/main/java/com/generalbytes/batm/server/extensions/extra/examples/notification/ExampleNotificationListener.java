package com.generalbytes.batm.server.extensions.extra.examples.notification;

import com.generalbytes.batm.server.extensions.AbstractNotificationListener;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.INotificationDetails;

import java.util.Map;

public class ExampleNotificationListener extends AbstractNotificationListener {

    private final IExtensionContext ctx;

    public ExampleNotificationListener(IExtensionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void transactionCashLimitReached(INotificationDetails notificationDetails) {
        Map<String, String> details = (Map<String, String>) notificationDetails.getAdditionalData().get("details");
        String identityEmail = details.get("identityEmail");
        String limitName = details.get("limitName");

        switch (limitName) {
            case "LIMIT_DAY":
                ctx.sendMailAsync("operator@example.com", identityEmail, "Bitcoin ATM Daily Limit Reached", "You have reached your daily limit, please come again later", null);
                break;
            case "LIMIT_TRANSACTION":
                ctx.sendMailAsync("operator@example.com", identityEmail, "Bitcoin ATM Transaction Limit Reached", "You have reached your transaction limit, please make a transaction for a smaller amount", null);
                break;
        }
    }


}
