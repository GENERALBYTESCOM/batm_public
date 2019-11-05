/*************************************************************************************
 * Copyright (C) 2014-2019 GENERAL BYTES s.r.o. All rights reserved.
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

package com.generalbytes.batm.server.extensions;

import java.io.IOException;
import java.net.InetSocketAddress;

public interface ITunnelManager {
    /**
     *
     * @param tunnelLogin Colon-separated tunnel parameters, e.g. "ssh:10.0.0.1:22222:password".
     *                    Can be null or empty when no tunnel is configured. In that case this method does nothing.
     * @param originalWalletAddress remote host and port where a wallet is listening
     * @return address (with a port) where it will be possible to reach the wallet.
     * If no tunnel was needed (not configured / the tunnelLogin parameter was empty), {@code originalWalletAddress} is returned.
     * If the tunnel was established then it returns localhost with a port where the tunnel is listening. This port is tunneled to the original wallet address.
     * @throws IOException if opening a tunnel is needed (configured) but opening it failed.
     */
    InetSocketAddress connectIfNeeded(String tunnelLogin, InetSocketAddress originalWalletAddress) throws IOException;
}
