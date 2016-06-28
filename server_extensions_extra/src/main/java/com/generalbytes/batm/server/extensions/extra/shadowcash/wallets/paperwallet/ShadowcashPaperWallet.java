package com.generalbytes.batm.server.extensions.extra.shadowcash.wallets.paperwallet;

import com.generalbytes.batm.server.extensions.IPaperWallet;
import lombok.Data;

/**
 * @author ludx
 */
@Data
public class ShadowcashPaperWallet implements IPaperWallet {

    private String address;
    private String privateKey;
    private String message;
    private String fileExtension = "zip";
    private String contentType = "application/zip";
    private byte[] content;

    public ShadowcashPaperWallet() {
    }

}
