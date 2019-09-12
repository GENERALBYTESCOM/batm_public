package com.generalbytes.batm.server.extensions.extra.dagcoin.dagpaper;

import com.generalbytes.batm.server.extensions.IPaperWallet;

public class DagPaperWallet implements IPaperWallet {
	private String cryptoCurrency;
    private byte[] content;
    private String address;
    private String privateKey;
    private String message;
    private String contentType;
    private String fileExtension;
    
    public DagPaperWallet(String cryptoCurrency, byte[] content, String address, String privateKey, String message, String contentType, String fileExtension) {
        this.cryptoCurrency = cryptoCurrency;
        this.content = content;
        this.address = address;
        this.privateKey = privateKey;
        this.message = message;
        this.contentType = contentType;
        this.fileExtension = fileExtension;
    }

	@Override
	public byte[] getContent() {
		return content;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public String getPrivateKey() {
		return privateKey;
	}

	@Override
	public String getFileExtension() {
		return fileExtension;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getCryptoCurrency() {
		return cryptoCurrency;
	}

}
