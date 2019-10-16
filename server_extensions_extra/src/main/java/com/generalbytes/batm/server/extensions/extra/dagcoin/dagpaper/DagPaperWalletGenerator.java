package com.generalbytes.batm.server.extensions.extra.dagcoin.dagpaper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IPaperWallet;
import com.generalbytes.batm.server.extensions.IPaperWalletGenerator;
import com.generalbytes.batm.server.extensions.extra.dagcoin.domain.DagCoinParameters;
import com.generalbytes.batm.server.extensions.extra.dagcoin.domain.PaperWalletResponse;
import com.generalbytes.batm.server.extensions.extra.dagcoin.exception.DagCoinRestClientException;
import com.generalbytes.batm.server.extensions.extra.dagcoin.service.DagCoinApiClientService;
//import com.generalbytes.bitrafael.api.wallet.bch.WalletToolsBCH;

public class DagPaperWalletGenerator implements IPaperWalletGenerator {
	
	private static final Logger log = LoggerFactory.getLogger(DagPaperWalletGenerator.class);
	private String prefix;
    private IExtensionContext ctx;
    private DagCoinApiClientService service;

	public DagPaperWalletGenerator(String prefix, IExtensionContext ctx, DagCoinParameters params) {
		this.prefix = prefix;
        this.ctx = ctx;
        try {
			this.service = new DagCoinApiClientService(params);
		} catch (DagCoinRestClientException e) {
			log.error("Error in instantiating DagCoinApiService - " + e.getErrorCode() + " :: " + e.getMessage());
			return;
		}
	}
	
	@Override
	public IPaperWallet generateWallet(String cryptoCurrency, String onetimePassword, String userLanguage) {
		log.info("Generating paper wallet for - " + cryptoCurrency);
		try {
			// get the cardID, wallet address and PIN from service
			PaperWalletResponse paperWalletResponse = this.service.generatePaperWallet();
			String address = paperWalletResponse.getWalletId();
			
			// Just for now 
			String privateKey = paperWalletResponse.getCardId();
			
			// sending un-encrypted ATM PIN
			byte[] content = paperWalletResponse.getAtmPin().getBytes();	
			
			// send wallet to customer
			String messageText = "New wallet " + address + " . Use the PIN to activate your NFC card.";
					
			return new DagPaperWallet(cryptoCurrency, content, address, privateKey, messageText, "application/zip", "zip");			
		} catch (DagCoinRestClientException e) {
			log.error("Error in generating paper wallet - " + e.getErrorCode() + " :: " + e.getMessage());
			return null;
		}	
	}
	
	/*
	@Override
	public IPaperWallet generateWallet(String cryptoCurrency, String onetimePassword, String userLanguage) {
		WalletToolsBCH walletTool = new WalletToolsBCH();
		String privateKey = walletTool.generateWalletPrivateKeyWithPrefix(prefix, CryptoCurrency.DAG.code);
		String address = walletTool.getWalletAddressFromPrivateKey(privateKey, CryptoCurrency.DAG.code);
		
		byte[] content = ctx.createPaperWallet7ZIP(privateKey, address, onetimePassword, cryptoCurrency);
		
		// send wallet to customer
		String messageText = "New wallet " + address + " . Use your one-time password to open the attachment.";
		
		return new DagPaperWallet(cryptoCurrency, content, address, privateKey, messageText, "application/zip", "zip");
	}
	*/

}
