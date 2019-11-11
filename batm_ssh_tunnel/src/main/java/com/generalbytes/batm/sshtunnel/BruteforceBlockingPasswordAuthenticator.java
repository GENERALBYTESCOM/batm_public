package com.generalbytes.batm.sshtunnel;

import org.apache.sshd.server.auth.AsyncAuthException;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.session.ServerSession;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * blocks the IP address after the specified amount of unsuccessful login attempts.
 * The number of attempts and blocked IP addresses are kept only in memory.
 * Unsuccessful attempts are reset after one successful attempt.
 *
 *
 */
public class BruteforceBlockingPasswordAuthenticator implements PasswordAuthenticator {
    private final Map<String, Integer> map = new ConcurrentHashMap<>();
    private final int maxAttempts;
    private final PasswordAuthenticator passwordAuthenticator;

    public BruteforceBlockingPasswordAuthenticator(int maxAttempts, PasswordAuthenticator passwordAuthenticator) {
        this.maxAttempts = maxAttempts;
        this.passwordAuthenticator = passwordAuthenticator;
    }

    @Override
    public boolean authenticate(String username, String password, ServerSession session) throws PasswordChangeRequiredException, AsyncAuthException {
        SocketAddress clientAddress = session.getClientAddress();
        if (!(clientAddress instanceof InetSocketAddress)) {
            return false;
        }
        final String key = ((InetSocketAddress) clientAddress).getHostString();
        Integer attempts = map.get(key);

        if (attempts != null && attempts > maxAttempts) {

            return false;
        }

        if (passwordAuthenticator.authenticate(username, password, session)) {
            map.remove(key);
            return true;
        }

        map.put(key, attempts == null ? 1 : attempts + 1);
        return false;
    }

}
