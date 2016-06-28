package com.generalbytes.batm.server.extensions.extra.shadowcash.wallets.paperwallet;

import com.generalbytes.batm.server.extensions.IPaperWallet;

/**
 * @author ludx
 */
public class ShadowcashPaperWallet implements IPaperWallet {

    private String address;
    private String privateKey;
    private String message;
    private String fileExtension = "zip";
    private String contentType = "application/zip";
    private byte[] content;

    public ShadowcashPaperWallet() {
    }

    @Override
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
