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

package com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.ICanSendMany;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOAddress;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIORequestSubmitTransaction;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponseAddresses;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponseBalance;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponsePrepareTransaction;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOResponseSubmitTransaction;
import com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.dto.BlockIOTransaction;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import si.mazi.rescu.ClientConfig;
import si.mazi.rescu.HttpStatusIOException;
import si.mazi.rescu.RestProxyFactory;

import javax.ws.rs.QueryParam;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.generalbytes.batm.server.extensions.extra.dogecoin.wallets.blockio.IBlockIO.*;

public class BlockIOWalletWithClientSideSigning implements IWallet, ICanSendMany {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.BlockIOWallet2");

    private final String priority;
    private final String fromLabel;
    private final BlockIOSignService signService;

    protected IBlockIO api;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public BlockIOWalletWithClientSideSigning(String apiKey, String pin, String priority, String fromLabel) {
        if (priority == null) {
            this.priority = PRIORITY_LOW;
        } else if (PRIORITY_LOW.equalsIgnoreCase(priority.trim())) {
            this.priority = PRIORITY_LOW;
        } else if (PRIORITY_MEDIUM.equalsIgnoreCase(priority.trim())) {
            this.priority = PRIORITY_MEDIUM;
        } else if (PRIORITY_HIGH.equalsIgnoreCase(priority.trim())) {
            this.priority = PRIORITY_HIGH;
        } else {
            this.priority = PRIORITY_LOW;
        }

        this.fromLabel = fromLabel;

        ClientConfig config = new ClientConfig();
        config.addDefaultParam(QueryParam.class, "api_key", apiKey);
        api = RestProxyFactory.createProxy(IBlockIO.class, "https://block.io", config);
        signService = new BlockIOSignService(pin);
    }

    @Override
    public Set<String> getCryptoCurrencies() {
        Set<String> result = new HashSet<>();
        result.add(CryptoCurrency.BTC.getCode());
        result.add(CryptoCurrency.LTC.getCode());
        result.add(CryptoCurrency.DOGE.getCode());
        return result;
    }

    @Override
    public String getPreferredCryptoCurrency() {
        return CryptoCurrency.BTC.getCode();
    }

    @Override
    public String getCryptoAddress(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        try {
            BlockIOResponseAddresses response = api.getAddresses();
            if (response != null && response.getData() != null && response.getData().getAddresses() != null && response.getData().getAddresses().length > 0) {
                if (fromLabel != null) {
                    for (BlockIOAddress address : response.getData().getAddresses()) {
                        if (fromLabel.equals(address.getLabel())) {
                            return address.getAddress();
                        }
                    }
                }
                return response.getData().getAddresses()[0].getAddress();
            }
        } catch (HttpStatusIOException e) {
            log.error("HTTP error in getCryptoAddress: {}", e.getHttpBody());
        } catch (Exception e) {
            log.error("Error", e);
        }

        return null;
    }


    @Override
    public BigDecimal getCryptoBalance(String cryptoCurrency) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        try {
            List<String> labels = fromLabel != null ? Collections.singletonList(fromLabel) : Collections.emptyList();
            BlockIOResponseBalance response = api.getAddressBalance(labels);
            if (response != null && response.getData() != null && response.getData().getAvailable_balance() != null) {
                return new BigDecimal(response.getData().getAvailable_balance());
            }
        } catch (HttpStatusIOException e) {
            log.error("HTTP error in getCryptoBalance: {}", e.getHttpBody());
        } catch (Exception e) {
            log.error("Error", e);
        }

        return null;
    }

    @Override
    public String sendMany(Collection<Transfer> transfers, String cryptoCurrency, String description) {
        if (!getCryptoCurrencies().contains(cryptoCurrency)) {
            return null;
        }
        try {
            // sum amounts for the same address - this wallet cannot send multiple amounts to the same address
            Map<String, BigDecimal> destinationAddressAmounts = transfers.stream()
                .collect(Collectors.toMap(Transfer::getDestinationAddress, Transfer::getAmount, BigDecimal::add));

            List<String> toAddresses = new ArrayList<>(destinationAddressAmounts.keySet());

            // get values in the same order as toAddresses
            // stream from list should be ordered, map.values() could potentially have different order
            List<BigDecimal> amounts = toAddresses.stream()
                .map(destinationAddressAmounts::get)
                .map(amount -> amount.setScale(8, RoundingMode.FLOOR))
                .collect(Collectors.toList());

            log.info("{} calling withdraw {} to {}", getClass().getSimpleName(), amounts, toAddresses);
            List<String> fromLabels = fromLabel == null ? null : Collections.nCopies(toAddresses.size(), fromLabel);

            BlockIOResponsePrepareTransaction response = api.prepareTransaction(fromLabels, amounts, toAddresses, priority);

            BlockIOTransaction transaction = signService.createAndSignTransaction(response);
            BlockIORequestSubmitTransaction request = new BlockIORequestSubmitTransaction(transaction);
            BlockIOResponseSubmitTransaction submitResponse = api.submitTransaction(request);

            if ("success".equals(submitResponse.getStatus()) && submitResponse.getData() != null) {
                return submitResponse.getData().getTxid();
            }
            return null;
        } catch (HttpStatusIOException e) {
            log.error("HTTP error in sendCoins: {}", e.getHttpBody());
        } catch (Exception e) {
            log.error("Error", e);
        }

        return null;
    }

    @Override
    public String sendCoins(String destinationAddress, BigDecimal amount, String cryptoCurrency, String description) {
        return sendMany(Collections.singleton(new Transfer(destinationAddress, amount)), cryptoCurrency, description);
    }

}