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
    public Object terminalsWithAvailableCash(@HeaderParam("X-Api-Key") String apiKey, @QueryParam("amount") BigDecimal amount,
                                             @QueryParam("fiat_currency")
        String fiatCurrency) {

        try {
            checkSecurity(apiKey);
            if (amount == null) {
                responseInvalidParameter("amount");
            }
            if (fiatCurrency == null) {
                responseInvalidParameter("fiat_currency");
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
        } catch (AuthenticationException e) {
            return responseInvalidApiKey();
        } catch (Throwable e) {
            log.error("Error - terminals with available cash", e);
        }
        return responseInternalServerError();
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
    public Object sellCrypto(@HeaderParam("X-Api-Key") String apiKey, @QueryParam("serial_number") String serialNumber,
                             @QueryParam("fiat_amount") BigDecimal fiatAmount, @QueryParam("fiat_currency") String fiatCurrency,
                             @QueryParam("crypto_amount") BigDecimal cryptoAmount, @QueryParam("crypto_currency") String cryptoCurrency,
                             @QueryParam("identity_public_id") String identityPublicId, @QueryParam("discount_code") String discountCode) {

        try {
            checkSecurity(apiKey);
            if (serialNumber == null) {
                responseInvalidParameter("serial_number");
            }
            if (fiatAmount == null) {
                responseInvalidParameter("fiat_amount");
            }
            if (fiatCurrency == null) {
                responseInvalidParameter("fiat_currency");
            }
            if (cryptoCurrency == null) {
                responseInvalidParameter("crypto_currency");
            }
            return SellExtensions.getExtensionContext().sellCrypto(serialNumber, fiatAmount, fiatCurrency, cryptoAmount, cryptoCurrency, identityPublicId, discountCode);

        } catch (AuthenticationException e) {
            return responseInvalidApiKey();

        } catch (Throwable e) {
            log.error("Error - sell crypto", e);
        }
        return responseInternalServerError();
    }

    /**
     * @param apiKey        Morphis API key
     * @param transactionId - Id of created transaction from sell_crypto
     * @return number with status of the transaction
     */
    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Object status(@HeaderParam("X-Api-Key") String apiKey, @QueryParam("transaction_id") String transactionId) {

        try {
            checkSecurity(apiKey);
            if (transactionId == null) {
                return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("{ \"error\": \"Missing parameter transaction_id\" }").build();
            }
            return SellExtensions.getExtensionContext().findTransactionByTransactionId(transactionId).getStatus();
        } catch (AuthenticationException e) {
            return responseInvalidApiKey();
        } catch (Throwable e) {
            log.error("Error - status", e);
        }
        return responseInternalServerError();
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
    public Object terminals(@HeaderParam("X-Api-Key") String apiKey) {

        try {
            IApiAccess iApiAccess = checkSecurity(apiKey);
            return getTerminalsByApiKey(iApiAccess);
        } catch (AuthenticationException e) {
            return responseInvalidApiKey();
        } catch (Throwable e) {
            log.error("Error - terminals", e);
        }
        return responseInternalServerError();
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
    private IApiAccess checkSecurity(String apiKey) throws AuthenticationException {
        IApiAccess iApiAccess = SellExtensions.getExtensionContext().getAPIAccessByKey(apiKey);
        if (iApiAccess == null) {
            throw new AuthenticationException("Authentication failed");
        }
        return iApiAccess;
    }

    private Response responseInvalidParameter(String paramName) {
        return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("{ \"error\": \"Parameter " + paramName + " can't be null\"}").build();
    }

    private Response responseInvalidApiKey() {
        return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity("{ \"error\": \"Invalid X-Api-Key\" }").build();
    }

    private Response responseInternalServerError() {
        return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity("{ \"error\": \"Internal server error\" }").build();
    }
}
