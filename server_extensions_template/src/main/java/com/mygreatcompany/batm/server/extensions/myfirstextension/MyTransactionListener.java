package com.mygreatcompany.batm.server.extensions.myfirstextension;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.ITransactionDetails;
import com.generalbytes.batm.server.extensions.ITransactionListener;

import java.util.Map;

public class MyTransactionListener implements ITransactionListener {
    private IExtensionContext ctx;

    public MyTransactionListener(IExtensionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Map<String, String> onTransactionCreated(ITransactionDetails transactionDetails) {
        System.out.println("Bravo! Transaction has been created! Here are the details: = " + transactionDetails);
        return null;
    }

    @Override
    public Map<String, String> onTransactionUpdated(ITransactionDetails transactionDetails) {
        System.out.println("Hey! Transaction has been updated! Here are the details: " + transactionDetails);
        return null;
    }
}
