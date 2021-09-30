package com.generalbytes.batm.server.extensions.website;

import com.generalbytes.batm.server.extensions.IApiAccess;
import com.generalbytes.batm.server.extensions.ITerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
     * @param apiKey Morphis API key
     * @return list of terminals that have specified cash available for sell transactions.
     */
    @GET
    @Path("/terminals_with_available_cash")
    @Produces(MediaType.APPLICATION_JSON)
    public Object terminalsWithAvailableCash(@QueryParam("amount") BigDecimal amount, @QueryParam("fiat_currency") String fiatCurrency, @QueryParam("api_key") String apiKey) {

        if (checkSecurity(apiKey)) {
            return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity("Unauthorized response").build();
        }
        if (amount == null || fiatCurrency == null) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Missing parameter amount or fiat_currency").build();
        }

        try {
            List<String> serialNumbers = SellExtensions.getExtensionContext().findTerminalsWithAvailableCashForSell(amount, fiatCurrency, null);
            List<ITerminal> filteredTerminals = new ArrayList<>();
            for (String serialNumber : serialNumbers) {
                ITerminal terminal = SellExtensions.getExtensionContext().findTerminalBySerialNumber(serialNumber);
                if (isOnline(terminal)) {
                    filteredTerminals.add(terminal);
                }
            }
            return filteredTerminals;

        } catch (Throwable e) {
            log.error("Error - terminals with available cash", e);
        }
        return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("ERROR").build();
    }

    /**
     * https://localhost:7743/extensions/website/sell_crypto
     * Method creates sell transaction
     * @param apiKey Morphis API key
     * @return ITransactionSellInfo
     */
    @GET
    @Path("/sell_crypto")
    @Produces(MediaType.APPLICATION_JSON)
    public Object sellCrypto(@QueryParam("api_key") String apiKey, @QueryParam("serial_number") String serialNumber, @QueryParam("fiat_amount") BigDecimal fiatAmount, @QueryParam("fiat_currency") String fiatCurrency, @QueryParam("crypto_amount") BigDecimal cryptoAmount, @QueryParam("crypto_currency") String cryptoCurrency, @QueryParam("identity_public_id") String identityPublicId, @QueryParam("discount_code") String discountCode) {

        if (checkSecurity(apiKey)) {
            return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity("Unauthorized response").build();
        }
        if (serialNumber == null || fiatAmount == null || fiatCurrency == null || cryptoAmount == null || cryptoCurrency == null) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Missing parameter").build();
        }

        try {
            return SellExtensions.getExtensionContext().sellCrypto(serialNumber, fiatAmount, fiatCurrency, cryptoAmount, cryptoCurrency, identityPublicId, discountCode);
        } catch (Throwable e) {
            log.error("Error - sell crypto", e);
        }
        return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("ERROR").build();
    }

    /**
     * @param apiKey Morphis API key
     * @param transactionId - Id of created transaction from sell_crypto
     * @return number with status of the transaction
     */
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Object status(@QueryParam("api_key") String apiKey, @QueryParam("transaction_id") String transactionId) {

        if (checkSecurity(apiKey)) {
            return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity("Unauthorized response").build();
        }
        if (transactionId == null) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Missing parameter transaction_id").build();
        }
        try {
            return SellExtensions.getExtensionContext().findTransactionByTransactionId(transactionId).getStatus();
        } catch (Throwable e) {
            log.error("Error - status", e);
        }
        return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("ERROR").build();
    }

    /**
     * https://localhost:7743/extensions/website/terminals
     * @param apiKey Morphis API key
     * @return list of active terminals ( active = pingDelay < 5 min ).
     */
    @GET
    @Path("/terminals")
    @Produces(MediaType.APPLICATION_JSON)
    public Object terminals(@QueryParam("api_key") String apiKey) {

        if (checkSecurity(apiKey)) {
            return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity("Unauthorized response").build();
        }
        try {
            IApiAccess iApiAccess = SellExtensions.getExtensionContext().getAPIAccessByKey(apiKey);
            return getTerminalsByApiKey(iApiAccess);
        } catch (Throwable e) {
            log.error("Error - terminals", e);
        }
        return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("ERROR").build();
    }

    /**
     * Method helps collect terminals with a same Morphis API Access in CAS
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
     * @return boolean
     */
    private boolean checkSecurity(String apiKey) {
        return apiKey == null || SellExtensions.getExtensionContext().getAPIAccessByKey(apiKey) == null;
    }
}
