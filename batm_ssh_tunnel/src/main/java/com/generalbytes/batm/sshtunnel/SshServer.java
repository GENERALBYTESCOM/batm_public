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
package com.generalbytes.batm.sshtunnel;

import org.apache.sshd.common.io.nio2.Nio2ServiceFactoryFactory;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.common.util.security.bouncycastle.BouncyCastleGeneratorHostKeyProvider;
import org.apache.sshd.server.forward.ForwardingFilter;
import org.apache.sshd.server.forward.TcpForwardingFilter;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SshServer {
    private static final String HOST_KEY_FILE = "host_key";
    private final int port;
    private final File configDir;

    public SshServer(File configDir, int port) {
        this.port = port;
        this.configDir = configDir;
    }

    public void start() throws IOException {
        final org.apache.sshd.server.SshServer sshd = org.apache.sshd.server.SshServer.setUpDefaultServer();
        sshd.setPort(port);
        sshd.setKeyPairProvider(new BouncyCastleGeneratorHostKeyProvider(new File(configDir, HOST_KEY_FILE).toPath()));
        sshd.setPasswordAuthenticator(new Auth(configDir).getPasswordAuthenticator());
        sshd.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, TimeUnit.MINUTES, 1);
        sshd.setForwardingFilter(ForwardingFilter.asForwardingFilter(null, null, TcpForwardingFilter.DEFAULT));
        sshd.setIoServiceFactoryFactory(new Nio2ServiceFactoryFactory());
        sshd.start();
    }
}
