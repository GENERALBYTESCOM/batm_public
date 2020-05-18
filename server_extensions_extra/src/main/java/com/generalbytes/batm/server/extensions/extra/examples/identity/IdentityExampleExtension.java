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
package com.generalbytes.batm.server.extensions.extra.examples.identity;

import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.aml.IExternalIdentity;
import com.generalbytes.batm.server.extensions.aml.IExternalIdentityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

// uncomment in batm-extensions.xml
public class IdentityExampleExtension extends AbstractExtension {
    Logger log = LoggerFactory.getLogger(IdentityExampleExtension.class);

    private static IExtensionContext ctx;

    @Override
    public void init(IExtensionContext ctx) {
        super.init(ctx);
        this.ctx = ctx;
    }

    @Override
    public String getName() {
        return "Identity Extension (example)";
    }

    @Override
    public Set<IRestService> getRestServices() {
        HashSet<IRestService> services = new HashSet<>();
        services.add(new IdentityExampleIRestService());
        return services;
    }

    public static IExtensionContext getExtensionContext() {
        return ctx;
    }

    @Override
    public Set<IExternalIdentityProvider> getIdentityProviders() {
        Set<IExternalIdentityProvider> ips = new HashSet<>();
        ips.add(new IExternalIdentityProvider() {
            @Override
            public IExternalIdentity findIdentityByExternalId(String identityExternalId) {
                return null;
            }

            @Override
            public IExternalIdentity findIdentityByPhoneNumber(String cellPhoneNumber) {
                if (cellPhoneNumber == null || !cellPhoneNumber.endsWith("123")) {
                    return null;
                }
                log.info("Returning External Identity");
                return new IExternalIdentity() {
                    @Override
                    public String getId() {
                        return "1";
                    }

                    @Override
                    public int getState() {
                        return STATE_REGISTERED;
                    }

                    @Override
                    public String getPhoneNumber() {
                        return cellPhoneNumber;
                    }

                    @Override
                    public String getEmail() {
                        return "k@k.com";
                    }

                    @Override
                    public String getFirstname() {
                        return "John";
                    }

                    @Override
                    public String getLastname() {
                        return "Doe";
                    }

                    @Override
                    public String getLanguage() {
                        return "en";
                    }
                };
            }

            @Override
            public IExternalIdentity findIdentityByEmail(String emailAddress) {
                return null;
            }

            @Override
            public boolean isPINCorrect(String identityExternalId, String pinEnteredByCustomer) {
                if (pinEnteredByCustomer.equalsIgnoreCase("1234")) {
                    log.debug("Customer " + identityExternalId + " entered correct PIN");
                    return true;
                }
                log.debug("Customer " + identityExternalId + " entered incorrect PIN");
                return false;
            }
        });
        return ips;
    }
}
