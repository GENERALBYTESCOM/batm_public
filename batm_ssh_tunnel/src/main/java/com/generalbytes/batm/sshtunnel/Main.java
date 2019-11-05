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

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class Main {

    private static final File CONFIG_DIR = new File(System.getProperty("user.home"), ".batm_ssh_tunnel");

    public static void main(String[] args) throws IOException, GeneralSecurityException {

        if (!CONFIG_DIR.exists() && !CONFIG_DIR.mkdirs()) {
            throw new IOException("Cannot create " + CONFIG_DIR.getAbsolutePath());
        }


        if (args.length > 0 && "init".equals(args[0])) {
            String password = new Auth(CONFIG_DIR).init();
            System.out.println(password); // intentionally stdout and not logging. The output is used in the install script

        } else {
            int port = Integer.parseInt(System.getProperty("batmSshTunnelPort", "22222"));
            new SshServer(CONFIG_DIR, port).start();

            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                }
            }
        }
    }

}