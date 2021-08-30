package com.generalbytes.batm.server.extensions.extra.lightningbitcoin.lnurl;

import com.generalbytes.batm.common.currencies.CryptoCurrency;
import com.generalbytes.batm.server.coinutil.LnInvoiceUtil;
import com.generalbytes.batm.server.coinutil.LnurlUtil;
import com.generalbytes.batm.server.extensions.IExtensionContext;
import com.generalbytes.batm.server.extensions.IRestService;
import com.generalbytes.batm.server.extensions.ITransactionDetails;
import com.generalbytes.batm.server.extensions.ITransactionRequest;
import com.generalbytes.batm.server.extensions.IWallet;
import com.generalbytes.batm.server.extensions.exceptions.UpdateException;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.LightningBitcoinExtension;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.lnurl.dto.ErrorLnurlResponse;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.lnurl.dto.OkLnurlResponse;
import com.generalbytes.batm.server.extensions.extra.lightningbitcoin.lnurl.dto.WithdrawLnurlResponse;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class LnurlRestService implements IRestService {
    private static final Logger log = LoggerFactory.getLogger(LnurlRestService.class);
    private static final String LBTC = CryptoCurrency.LBTC.getCode();
    private static final long WITHDRAW_EXPIRY_HOURS = 3 * 24;

    private final LnInvoiceUtil lnInvoiceUtil = new LnInvoiceUtil();
    private final LnurlUtil lnurlUtil = new LnurlUtil();
    private final IExtensionContext extensionContext = LightningBitcoinExtension.getExtensionContext();

    @Override
    public String getPrefixPath() {
        return "lnurl";
    }

    @Override
    public Class getImplementation() {
        return LnurlRestService.class;
    }

    @GET
    public Object ping() {
        return "BATM LNURL REST Service";
    }

    /**
     * This endpoint is called by the user's wallet first, right after scanning the LNURL QR code.
     * It just sends the data from the request back to the client.
     * We encode everything that's needed in the QR code to save one DB query here.
     * The DB query and security checks are done in the confirm endpoint.
     * <p>
     * After getting a response from this endpoint the wallet will display
     * a screen where the user can confirm the withdraw amount (millisats).
     * <p>
     * When the user confirms, the wallet will generate a LN invoice
     * for the given amount of millisats and send it to the callback URL.
     *
     * @return an object containing:
     * - callback URL where to send an invoice to (the withdraw-confirm endpoint)
     * - withdrawable amount minimum and maximum (which are equal in our case - the user must withdraw full transaction amount)
     * - description that *could* be displayed to the user in their wallet
     */
    @GET
    @Path(LnurlUtil.WITHDRAW_PATH)
    public Object withdraw(@QueryParam("uuid") String uuid, @QueryParam("rid") String rid, @QueryParam("millisats") Long millisats) {
        log.info("Processing LNURL withdraw request, uuid: {}, rid: {}, millisats: {}", uuid == null ? null : "***", rid, millisats);

        try {
            Objects.requireNonNull(uuid, "uuid cannot be null");
            Objects.requireNonNull(rid, "rid cannot be null");
            Objects.requireNonNull(millisats, "millisats cannot be null");

            String callbackUrl = Objects.requireNonNull(lnurlUtil.getConfirmCallbackUrl(uuid, rid), "callback url cannot be null");
            WithdrawLnurlResponse response = new WithdrawLnurlResponse(millisats, millisats, rid, callbackUrl);
            log.debug("Sending LNURL withdraw response for rid: {} {}", rid, response);
            return response;
        } catch (RuntimeException e) {
            log.error("Error processing LNURL withdraw request, rid: {}", rid, e);
            return new ErrorLnurlResponse("Error processing LNURL " + Strings.nullToEmpty(rid));
        }
    }

    /**
     * This endpoint is called after the user confirms the withdraw amount in their wallet.
     *
     * @param uuid    used to match the transaction, taken by the user's wallet from callback url
     * @param rid     for logging, taken by the user's wallet from callback url
     * @param invoice payment request sent by the user's wallet to be paid by the server; the name "pr" is specified by lnurl-rfc
     */
    @GET
    @Path(LnurlUtil.WITHDRAW_CONFIRM_PATH)
    public Object withdrawConfirm(@QueryParam("uuid") String uuid, @QueryParam("rid") String rid, @QueryParam("pr") String invoice) {
        log.info("Processing LNURL withdraw-confirm request, uuid: {}, rid: {}, invoice: {}", uuid == null ? null : "***", rid, invoice);
        try {
            Objects.requireNonNull(uuid, "uuid cannot be null");
            Objects.requireNonNull(rid, "rid cannot be null");
            Objects.requireNonNull(invoice, "invoice cannot be null");

            synchronized (uuid.intern()) { // do not allow withdrawing the same transaction multiple times in parallel
                ITransactionDetails transaction = findTransaction(uuid, rid);
                validateInvoice(invoice, transaction.getCryptoAmount());
                IWallet wallet = getWallet(transaction.getTerminalSerialNumber());
                payInvoice(invoice, rid, transaction.getCryptoAmount(), wallet);
            }

            log.debug("Sending LNURL withdraw-confirm OK response, rid: {}", rid);
            return new OkLnurlResponse();
        } catch (RuntimeException e) {
            log.error("Error processing LNURL withdraw-confirm request, rid: {}", rid, e);
            return new ErrorLnurlResponse("Error processing LNURL " + Strings.nullToEmpty(rid));
        } catch (LnurlRestServiceException e) {
            log.error("Error processing LNURL withdraw-confirm request, rid: {}", rid, e);
            return new ErrorLnurlResponse(e.getClientMessage() + " " + Strings.nullToEmpty(rid));
        }
    }

    private ITransactionDetails findTransaction(String uuid, String rid) throws LnurlRestServiceException {
        ITransactionDetails trx = extensionContext.findTransactionByTransactionUUIDAndType(uuid, ITransactionRequest.TYPE_BUY_CRYPTO);

        if (trx == null) {
            log.error("Transaction not found, rid: {}", rid);
            throw new LnurlRestServiceException();
        }

        log.info("Transaction {} found, {} {}, status: {}, errorCode: {}, terminal SN: {}, server time: {}",
            trx.getRemoteTransactionId(), trx.getCryptoAmount(), trx.getCryptoCurrency(), trx.getStatus(), trx.getErrorCode(), trx.getTerminalSerialNumber(), trx.getServerTime());

        if (!trx.getRemoteTransactionId().equals(rid)
            || trx.getCryptoAmount().compareTo(BigDecimal.ZERO) <= 0
            || !trx.getCryptoCurrency().equals(LBTC)) {

            log.error("Incorrect transaction found, rid: {}", rid);
            throw new LnurlRestServiceException();
        }

        long millis = System.currentTimeMillis() - trx.getServerTime().getTime();
        long hours = TimeUnit.HOURS.convert(millis, TimeUnit.MILLISECONDS);
        if (hours >= WITHDRAW_EXPIRY_HOURS) {
            log.error("Transaction too old: {}h, rid: {}", hours, rid);
            throw new LnurlRestServiceException("Expired");
        }

        if (trx.getStatus() != ITransactionDetails.STATUS_BUY_IN_PROGRESS) {
            if (trx.getStatus() == ITransactionDetails.STATUS_BUY_COMPLETED) {
                log.error("Already withdrawn, rid: {}", rid);
                throw new LnurlRestServiceException("Already withdrawn");
            }

            log.error("Transaction status invalid, rid: {}", rid);
            throw new LnurlRestServiceException();
        }
        return trx;
    }

    /**
     * @return non null payment id for successful transactions, LN wallets should return "preimage" that could be used as a proof of payment
     * @throws RuntimeException when paying the invoice fails
     */
    private String sendCoins(String invoice, String rid, BigDecimal cryptoAmount, IWallet wallet) throws RuntimeException {
        log.info("Paying invoice, wallet: {}, rid: {}", wallet.getClass().getSimpleName(), rid);
        String sendingId = wallet.sendCoins(invoice, cryptoAmount, LBTC, rid);
        if (sendingId == null) {
            throw new RuntimeException("Error sending from wallet");
        }
        return sendingId;
    }

    /**
     * Try to pay the invoice and mark the transaction as COMPLETED if sending is successful.
     * Mark the transaction as failed when sending fails (or when marking as completed fails)
     * so it cannot be withdrawn multiple times in case it was actually sent.
     *
     * @throws RuntimeException when paying invoice was not finished successfully or it could not be marked as completed
     */
    private void payInvoice(String invoice, String rid, BigDecimal cryptoAmount, IWallet wallet) throws RuntimeException {
        try {
            extensionContext.updateTransaction(rid, null, invoice);
            String sendingId = sendCoins(invoice, rid, cryptoAmount, wallet);
            log.info("Invoice paid, setting transaction as COMPLETED, rid: {}, sendingId: {}", rid, sendingId);
            extensionContext.updateTransaction(rid, ITransactionDetails.STATUS_BUY_COMPLETED, sendingId);
        } catch (RuntimeException | UpdateException e) {
            try {
                log.warn("Marking transaction as failed, rid: {}", rid);
                extensionContext.updateTransaction(rid, ITransactionDetails.STATUS_BUY_ERROR, null);
            } catch (UpdateException e2) {
                log.error("Error marking transaction as failed! {}", rid, e2); // log and eat the second exception
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * @throws RuntimeException if amount does not match or if invoice format is invalid or the amount cannot be parsed
     */
    private void validateInvoice(String invoice, BigDecimal cryptoAmount) throws RuntimeException {
        BigDecimal invoiceAmount = lnInvoiceUtil.getAmount(invoice);
        if (invoiceAmount.compareTo(cryptoAmount) != 0) {
            throw new IllegalArgumentException("Invoice amount is different from transaction amount");
        }
    }

    private IWallet getWallet(String terminalSerialNumber) throws RuntimeException {
        return Objects.requireNonNull(extensionContext.findBuyWallet(terminalSerialNumber, LBTC), "LBTC Buy wallet not found for " + terminalSerialNumber);
    }

}
