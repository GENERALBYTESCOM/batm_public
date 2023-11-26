package com.mygreatcompany.batm.server.extensions.myfirstextension;

import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.ITransactionDetails;
import com.generalbytes.batm.server.extensions.ITransactionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MyTransactionListener implements ITransactionListener {
    private IExtensionContext ctx;
    protected final Logger log = LoggerFactory.getLogger("batm.master.myextension");


    public MyTransactionListener(IExtensionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Map<String, String> onTransactionCreated(ITransactionDetails transactionDetails) {
        log.info("Bravo! Transaction has been created! Here are the details: = " + transactionDetails);
        return null;
    }

    @Override
    public Map<String, String> onTransactionUpdated(ITransactionDetails transactionDetails) {
        log.info("Hey! Transaction has been updated! Here are the details: " + transactionDetails);
        return null;
    }
}
