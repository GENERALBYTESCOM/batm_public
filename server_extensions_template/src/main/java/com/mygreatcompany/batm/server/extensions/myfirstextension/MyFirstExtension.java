package com.mygreatcompany.batm.server.extensions.myfirstextension;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyFirstExtension extends AbstractExtension {

    protected final Logger log = LoggerFactory.getLogger("batm.master.myextension");


    @Override
    public String getName() {
        return "My first extension";
    }


    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        log.debug("MyFirst extension initialized. Adding listener");
        ctx.addTransactionListener(new MyTransactionListener(ctx));
    }
}
