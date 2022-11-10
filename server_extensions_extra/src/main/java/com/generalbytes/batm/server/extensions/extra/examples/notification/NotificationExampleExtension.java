/*************************************************************************************
 * Copyright (C) 2014-2020 GENERAL BYTES s.r.o. All rights reserved.
 *
 * This software may be distributed and modified under the terms of the GNU
 * General Public License version 2 (GPL2) as published by the Free Software
 * Foundation and appearing in the file GPL2.TXT included in the packaging of
 * this file. Please note that GPL2 Section 2[b] requires that all works based
 * on this software must also be made publicly available under the terms of
 * the GPL2 ("Copyleft").
 *
 * Contact information
 * -------------------
 *
 * GENERAL BYTES s.r.o.
 * Web      :  http://www.generalbytes.com
 *
 ************************************************************************************/
package com.generalbytes.batm.server.extensions.extra.examples.notification;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;

/*
Enable this extension by adding the following line to /batm/config/extensions
com.generalbytes.batm.server.extensions.extra.examples.notification.NotificationExampleExtension.autoload=true
*/
public class NotificationExampleExtension extends AbstractExtension {

    @Override
    public String getName() {
        return "BATM Example extension that reacts to notifications";
    }

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        ctx.addNotificationListener(new ExampleNotificationListener(ctx));
    }
}
