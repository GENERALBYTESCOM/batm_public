package com.generalbytes.batm.server.extensions.extra.cardano;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.AbstractExtension;
import com.generalbytes.batm.server.extensions.ExtensionsUtil;
import com.generalbytes.batm.server.extensions.ICryptoAddressValidator;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.cardano.wallets.CardanoWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.StringTokenizer;

public class CardanoExtension extends AbstractExtension {

    private static final Logger log = LoggerFactory.getLogger(CardanoExtension.class);

    @Override
    public String getName() {
        return "BATM Cardano extra extension";
    }

    @Override
    public IWallet createWallet(String walletLogin, String tunnelPassword) {
        if (walletLogin != null && !walletLogin.trim().isEmpty()) {
            StringTokenizer st = new StringTokenizer(walletLogin, ":");
            String walletType = st.nextToken();

            if ("cardano".equalsIgnoreCase(walletType)) {
                //"cardano:protocol:host:port:wallet_id:wallet_passphrase"
                try {
                    String protocol = st.nextToken();
                    String host = st.nextToken();
                    int port = Integer.parseInt(st.nextToken());
                    String walletId = st.nextToken();
                    String passphrase = st.nextToken();

                    InetSocketAddress tunnelAddress = ctx.getTunnelManager().connectIfNeeded(
                        walletLogin, tunnelPassword, InetSocketAddress.createUnresolved(host, port)
                    );
                    host = tunnelAddress.getHostString();
                    port = tunnelAddress.getPort();

                    if (protocol != null && host != null && walletId != null && passphrase != null) {
                        return new CardanoWallet(protocol, host, port, walletId, passphrase);
                    }
                } catch (IOException e) {
                    log.warn("createWallet failed for prefix: {}", ExtensionsUtil.getPrefixWithCountOfParameters(walletLogin));
                }
            }
        }
        return null;
    }

    @Override
    public ICryptoAddressValidator createAddressValidator(String cryptoCurrency) {
        if (CryptoCurrency.ADA.getCode().equalsIgnoreCase(cryptoCurrency)) {
            return new CardanoAddressValidator();
        }
        return null;
    }
}
