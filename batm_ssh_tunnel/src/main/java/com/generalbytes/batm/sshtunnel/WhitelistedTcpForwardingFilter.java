package com.generalbytes.batm.sshtunnel;

import org.apache.sshd.common.session.Session;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.apache.sshd.server.forward.TcpForwardingFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;
import java.util.stream.Collectors;

public class WhitelistedTcpForwardingFilter implements TcpForwardingFilter {
    private static final String WHITELIST_FILE = "port_whitelist";
    private final Set<Integer> whitelist;

    public WhitelistedTcpForwardingFilter(File configDir) throws IOException {
        File file = new File(configDir, WHITELIST_FILE);
        if (!file.exists()) {
            whitelist = null;
        } else {
            whitelist = Files.readAllLines(file.toPath()).stream()
                .map(String::trim)
                .filter(s -> !s.startsWith("#"))
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        }
    }

    @Override
    public boolean canListen(SshdSocketAddress address, Session session) {
        return false;
    }

    @Override
    public boolean canConnect(Type type, SshdSocketAddress address, Session session) {
        return whitelist == null || whitelist.contains(address.getPort());
    }
}
