package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets;

import com.generalbytes.batm.server.extensions.ILightningChannel;
import com.generalbytes.batm.server.extensions.ThrowingSupplier;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.wallets.lnd.LndWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class DemoLightningWallet extends AbstractLightningWallet {
    private static final Logger log = LoggerFactory.getLogger(LndWallet.class);

    @Override
    public BigDecimal getReceivedAmount(String invoice, String cryptoCurrency) {
        return BigDecimal.ONE;
    }

    @Override
    public String getInvoice(BigDecimal cryptoAmount, String cryptoCurrency, Long paymentValidityInSec, String description) {
        // zero amount expired invoice
        return "lnbc1pszzddupp58sz67nzrdegvuxlspz5yt8kw5fkexdke9jjmqtqwtwnkrzu6l2wqdqu2askcmr9wssx7e3q2dshgmmndp5scqzpgxqyz5vqsp5zkszu3wx2zvudsjfvdmqwkw9fvvlxqsehhqzk3fmycuqfnnc78lq9qy9qsq4pgjvc87m76kf8zwhjkrxjhxjwj22axmnfc39nvz4hekcpw289esgxaythlpujj9js24nsv9nv7djsp7pddsud46f2544yl5ksyrlcgqjr4e53";
    }

    @Override
    public List<? extends ILightningChannel> getChannels() {
        return Arrays.asList(
            new DemoLightningChannel(10_00000000_000L, 20_00000000_000L),
            new DemoLightningChannel(50_000L, 20000_000L));
    }

    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        return BigDecimal.TEN;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        return "123456";
    }

    @Override
    public String getPubKey() {
        return "deadbeefcafebabe000aeb9e96530e7b00e6fa03b571d235f6b4e68cfb4ef9097c";
    }

    @Override
    protected <T> T callChecked(ThrowingSupplier<T> supplier) {
           try {
            return supplier.get();
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    private class DemoLightningChannel implements ILightningChannel {

        private final long balanceMsat;
        private final long capacityMsat;

        private DemoLightningChannel(long balanceMsat, long capacityMsat) {
            this.balanceMsat = balanceMsat;
            this.capacityMsat = capacityMsat;
        }

        @Override
        public String getShortChannelId() {
            return "111111x222x0";
        }

        @Override
        public boolean isOnline() {
            return true;
        }

        @Override
        public String getRemoteNodeId() {
            return "00000007fa8bfad8675aeb9e96530e7b00e6fa03b571d235f6b4e68cfb4ef9097c";
        }

        @Override
        public String getRemoteNodeAlias() {
            return "remotenode";
        }

        @Override
        public String getLocalNodeId() {
            return getPubKey();
        }

        @Override
        public String getLocalNodeAlias() {
            return "localnode";
        }

        @Override
        public boolean isLocalFunder() {
            return true;
        }

        @Override
        public long getBalanceMsat() {
            return balanceMsat;
        }

        @Override
        public long getCapacityMsat() {
            return capacityMsat;
        }
    }
}
