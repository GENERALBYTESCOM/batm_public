package com.generalbytes.batm.server.extensions.website;

import com.generalbytes.batm.server.extensions.IApiAccess;
import com.generalbytes.batm.server.extensions.ITerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("/")
public class RestServiceWebsite {
    private static final Logger log = LoggerFactory.getLogger("batm.master.operators_sample_website");
    private static long pingDelay = 1000 * 60 * 5;

    /**
     * https://localhost:7743/extensions/website/terminals_with_available_cash
     *
     * @param apiKey Morphis API key
     * @return list of terminals that have specified cash available for sell transactions.
     */
    @GET
    @Path("/terminals-with-available-cash")
    @Produces(MediaType.APPLICATION_JSON)
    public Object terminalsWithAvailableCash(@HeaderParam("x_api_key") String apiKey, @QueryParam("amount") BigDecimal amount, @QueryParam("fiat_currency")
        String fiatCurrency) {

        try {
            this.checkSecurity(apiKey);
            if (amount == null) {
                this.responseBadRequest("amount");
            }
            if (fiatCurrency == null) {
                this.responseBadRequest("fiat_currency");
            }
            List<String> serialNumbers = SellExtensions.getExtensionContext().findTerminalsWithAvailableCashForSell(amount, fiatCurrency, null);
            List<ITerminal> filteredTerminals = new ArrayList<>();
            for (String serialNumber : serialNumbers) {
                ITerminal terminal = SellExtensions.getExtensionContext().findTerminalBySerialNumber(serialNumber);
                if (isOnline(terminal)) {
                    filteredTerminals.add(terminal);
                }
            }
            return filteredTerminals;
        } catch (NullApiKeyException e) {
            return this.responseInvalidApiKey();
        } catch (Throwable e) {
            log.error("Error - terminals with available cash", e);
        }
        return this.responseExpectationFailed();
    }

    /**
     * https://localhost:7743/extensions/website/sell-crypto
     * Method creates sell transaction
     *
     * @param apiKey Morphis API key
     * @return ITransactionSellInfo
     */
    @GET
    @Path("/sell-crypto")
    @Produces(MediaType.APPLICATION_JSON)
    public Object sellCrypto(@HeaderParam("x_api_key") String apiKey, @QueryParam("serial_number") String serialNumber,
                             @QueryParam("fiat_amount") BigDecimal fiatAmount, @QueryParam("fiat_currency") String fiatCurrency,
                             @QueryParam("crypto_amount") BigDecimal cryptoAmount, @QueryParam("crypto_currency") String cryptoCurrency,
                             @QueryParam("identity_public_id") String identityPublicId, @QueryParam("discount_code") String discountCode) {

        try {
            this.checkSecurity(apiKey);
            if (serialNumber == null) {
                this.responseBadRequest("serial_number");
            }
            if (fiatAmount == null) {
                this.responseBadRequest("fiat_amount");
            }
            if (fiatCurrency == null) {
                this.responseBadRequest("fiat_currency");
            }
            if (cryptoCurrency == null) {
                this.responseBadRequest("crypto_currency");
            }
            return SellExtensions.getExtensionContext().sellCrypto(serialNumber, fiatAmount, fiatCurrency, cryptoAmount, cryptoCurrency, identityPublicId, discountCode);

        } catch (NullApiKeyException e) {
            return this.responseInvalidApiKey();

        } catch (Throwable e) {
            log.error("Error - sell crypto", e);
        }
        return this.responseExpectationFailed();
    }

    /**
     * @param apiKey        Morphis API key
     * @param transactionId - Id of created transaction from sell_crypto
     * @return number with status of the transaction
     */
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Object status(@HeaderParam("x_api_key") String apiKey, @QueryParam("transaction_id") String transactionId) {


        try {
            this.checkSecurity(apiKey);
            if (transactionId == null) {
                return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("{ \"error\": \"Missing parameter transaction_id\" }").build();
            }
            return SellExtensions.getExtensionContext().findTransactionByTransactionId(transactionId).getStatus();
        } catch (NullApiKeyException e) {
            return this.responseInvalidApiKey();
        } catch (Throwable e) {
            log.error("Error - status", e);
        }
        return this.responseExpectationFailed();
    }

    /**
     * https://localhost:7743/extensions/website/terminals
     *
     * @param apiKey Morphis API key
     * @return list of active terminals ( active = pingDelay < 5 min ).
     */
    @GET
    @Path("/terminals")
    @Produces(MediaType.APPLICATION_JSON)
    public Object terminals(@HeaderParam("x_api_key") String apiKey) {

        try {
            IApiAccess iApiAccess = this.checkSecurity(apiKey);
            return getTerminalsByApiKey(iApiAccess);
        } catch (NullApiKeyException e) {
            return this.responseInvalidApiKey();
        } catch (Throwable e) {
            log.error("Error - terminals", e);
        }
        return this.responseExpectationFailed();
    }

    /**
     * Method helps collect terminals with a same Morphis API Access in CAS
     *
     * @param iApiAccess - Morphis API key
     * @return List<ITerminal>
     */
    private List<ITerminal> getTerminalsByApiKey(IApiAccess iApiAccess) {
        Collection<String> terminals = iApiAccess.getTerminalSerialNumbers();
        List<ITerminal> filteredTerminals = new ArrayList<>();
        terminals.forEach(terminalSerial -> {
            ITerminal terminal = SellExtensions.getExtensionContext().findTerminalBySerialNumber(terminalSerial);
            if (isOnline(terminal)) {
                filteredTerminals.add(terminal);
            }
        });
        return filteredTerminals;
    }

    /**
     * Method filters terminals with long ping delay
     *
     * @param terminal - Iterminal terminal
     * @return boolean
     */
    private boolean isOnline(ITerminal terminal) {
        long now = System.currentTimeMillis();
        return terminal.getLastPingAt() != null
            && (terminal.getLastPingAt().getTime() + pingDelay) > now;
    }

    /**
     * @param apiKey - Morphis API key
     * @return IApiAccess - Authenticated API key
     */
    private IApiAccess checkSecurity(String apiKey) throws NullApiKeyException {
        IApiAccess iApiAccess = SellExtensions.getExtensionContext().getAPIAccessByKey(apiKey);
        if (iApiAccess == null) {
            throw new NullApiKeyException();
        }
        return iApiAccess;
    }

    private Response responseBadRequest(String paramName) {
        return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("{ \"error\": \"Parameter " + paramName + " can't be null\"}").build();
    }

    private Response responseInvalidApiKey() {
        return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity("{ \"error\": \"Invalid x-api-key\" }").build();
    }

    private Response responseExpectationFailed() {
        return Response.status(HttpServletResponse.SC_EXPECTATION_FAILED).entity("{ \"error\": \"Expectation failed\" }").build();
    }
}
