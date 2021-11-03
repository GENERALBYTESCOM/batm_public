package com.generalbytes.batm.server.extensions.website;

import com.generalbytes.batm.server.extensions.ThirdPartyType;
import com.generalbytes.batm.server.extensions.IApiAccess;
import com.generalbytes.batm.server.extensions.ITerminal;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.*;

@Path("/")
public class RestServiceWebsite {
    private static final Logger log = LoggerFactory.getLogger("batm.master.operators_sample_website");
    private static long pingDelay = 1000 * 60 * 5;

    /**
     * https://localhost:7743/extensions/website/terminals_with_available_cash
     *
     * @param apiKey - key generated in ThirdParty/Operators Sample Website
     * @return list of terminals that have specified cash available for sell transactions.
     */
    @GET
    @Path("/terminals-with-available-cash")
    @Produces(MediaType.APPLICATION_JSON)
    public Object terminalsWithAvailableCash(@HeaderParam("X-Api-Key") String apiKey, @QueryParam("amount") BigDecimal amount,
                                             @QueryParam("fiat_currency") String fiatCurrency) {

        try {
            checkSecurity(apiKey);
            List<String> params = new ArrayList<>();
            if (amount == null) {
                params.add("amount");
            }
            if (fiatCurrency == null || fiatCurrency.trim().isEmpty()) {
                params.add("fiat_currency");
            }
            if (params.size() > 0) {
                return responseInvalidParameter(params);
            }
            List<String> serialNumbers = SellExtension.getExtensionContext().findTerminalsWithAvailableCashForSell(amount, fiatCurrency, null);
            List<ITerminal> filteredTerminals = new ArrayList<>();
            for (String serialNumber : serialNumbers) {
                ITerminal terminal = SellExtension.getExtensionContext().findTerminalBySerialNumber(serialNumber);
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
     * @param apiKey - key generated in ThirdParty/Operators Sample Website
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
            List<String> params = new ArrayList<>();
            if (serialNumber == null || serialNumber.trim().isEmpty()) {
                params.add("serial_number");
            }
            if (fiatAmount == null) {
                params.add("fiat_amount");
            }
            if (fiatCurrency == null || fiatCurrency.trim().isEmpty()) {
                params.add("fiat_currency");
            }
            if (cryptoCurrency == null || cryptoCurrency.trim().isEmpty()) {
                params.add("crypto_currency");
            }
            if (params.size() > 0) {
                return responseInvalidParameter(params);
            }
            return SellExtension.getExtensionContext().sellCrypto(serialNumber, fiatAmount, fiatCurrency, cryptoAmount, cryptoCurrency, identityPublicId, discountCode);

        } catch (AuthenticationException e) {
            return responseInvalidApiKey();

        } catch (Throwable e) {
            log.error("Error - sell crypto", e);
        }
        return responseInternalServerError();
    }

    /**
     * @param apiKey - key generated in ThirdParty/Operators Sample Website
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
            return SellExtension.getExtensionContext().findTransactionByTransactionId(transactionId).getStatus();
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
     * @param apiKey - key generated in ThirdParty/Operators Sample Website
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
     * Method helps collect terminals with a same ThirdParty/Operators sample website API Access in CAS
     *
     * @param iApiAccess - ThirdParty API key
     * @return List<ITerminal>
     */
    private List<ITerminal> getTerminalsByApiKey(IApiAccess iApiAccess) {
        Collection<String> terminals = iApiAccess.getTerminalSerialNumbers();
        List<ITerminal> filteredTerminals = new ArrayList<>();
        terminals.forEach(terminalSerial -> {
            ITerminal terminal = SellExtension.getExtensionContext().findTerminalBySerialNumber(terminalSerial);
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
     * @param apiKey - key generated in Third Party / Operators sample website (OSW)
     * @return IApiAccess - Authenticated API key
     */
    private IApiAccess checkSecurity(String apiKey) throws AuthenticationException {
        IApiAccess iApiAccess = SellExtension.getExtensionContext().getAPIAccessByKey(apiKey, ThirdPartyType.OSW.getCode());
        if (iApiAccess == null) {
            throw new AuthenticationException("Authentication failed");
        }
        return iApiAccess;
    }

    private Response responseInvalidParameter(List<String> params) {
        Map<String, List<String>> map = new HashMap<>();
        map.put("missingParameter", params);
        return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(createJsonString(map)).build();
    }

    private Response responseInvalidApiKey() {
        Map<String, String> map = new HashMap<>();
        map.put("error", "Invalid X-Api-Key");
        return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity(createJsonString(map)).build();
    }

    private Response responseInternalServerError() {
        Map<String, String> map = new HashMap<>();
        map.put("error", "Internal server error");
        return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(createJsonString(map)).build();
    }

    private String createJsonString(Map map) {
        Gson converter = new Gson();
        return converter.toJson(map);
    }
}
