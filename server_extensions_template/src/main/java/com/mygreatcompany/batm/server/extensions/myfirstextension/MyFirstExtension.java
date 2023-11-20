package com.mygreatcompany.batm.server.extensions.myfirstextension;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;

public class MyFirstExtension extends AbstractExtension {

    @Override
    public String getName() {
        return "My first extension";
    }

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        ctx.addTransactionListener(new MyTransactionListener(ctx));
    }
}
