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
package com.generalbytes.batm.server.extensions.extra.nano;

import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.extra.nano.rpc.NanoRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NanoAddressValidator implements ICryptoAddressValidator {

    private static final Logger log = LoggerFactory.getLogger(NanoAddressValidator.class);

    private final NanoExtensionContext context;

    public NanoAddressValidator(NanoExtensionContext context) {
        this.context = context;
    }


    @Override
    public boolean isAddressValid(String address) {
        String parsedAddr;
        try {
            parsedAddr = context.getUtil().parseAddress(address);
        } catch (IllegalArgumentException e) {
            return false; // Didn't match basic regex pattern
        }

        NanoRpcClient rpcClient = context.getRpcClient();
        if (rpcClient == null) {
            // RPC not configured - assume valid (note: checksum is NOT validated!)
            log.debug("Blindly assuming Nano account {} is valid.", parsedAddr);
            return true;
        } else {
            // Validate on node
            try {
                return rpcClient.isAddressValid(parsedAddr);
            } catch (IOException | NanoRpcClient.RpcException e) {
                // Hopefully shouldn't happen. Address may be valid, but we'll assume it isn't for this case.
                log.warn("Couldn't validate nano address over RPC.", e);
                return false;
            }
        }
    }

    @Override
    public boolean isPaperWalletSupported() {
        return true;
    }

    @Override
    public boolean mustBeBase58Address() {
        return false;
    }

}
