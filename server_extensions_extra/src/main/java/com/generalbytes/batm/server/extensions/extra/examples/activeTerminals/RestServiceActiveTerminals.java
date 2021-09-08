package com.generalbytes.batm.server.extensions.extra.examples.activeTerminals;

import com.generalbytes.batm.server.extensions.IApiAccess;
import com.generalbytes.batm.server.extensions.ITerminal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * REST service implementation class that uses JSR-000311 JAX-RS
 */
@Path("/")
public class RestServiceActiveTerminals {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.activeTerminals.ActiveTerminalsExtension");
    private static long pingDelay = 300000; // 300000 ms = 5min

    @GET
    @Path("/terminals")
    @Produces(MediaType.APPLICATION_JSON)
    /**
     * https://192.168.51.152:7743/extensions/example/active/terminals
     * Returns list of terminals and their locations plus other information. https://localhost:7743/extensions/example/active/terminals
     */
    public Object terminals(@QueryParam("api_key") String apiKey) {
        try {
            IApiAccess api_key = ActiveTerminalsExtension.getExtensionContext().getAPIAccessByKey(apiKey);
            if (api_key != null) {
                List<ITerminal> terminals = getTerminalsByApiKey(api_key);
                return terminals;
            }
        } catch (Throwable e) {
            log.debug("Wrong access to active terminals RestApi ", e);
        }
        return "wrong api key";
    }

    /**
     * Method helps collect terminals with a same Morphis API Access in CAS
     *
     * @param api_key
     * @return List<ITerminal>
     */
    private List<ITerminal> getTerminalsByApiKey(IApiAccess api_key) {
        Collection terminalsCollection = api_key.getTerminalSerialNumbers();
        List<ITerminal> filteredTerminals = new ArrayList<>();
        terminalsCollection.forEach(terminalSerial -> {
            ITerminal terminal = ActiveTerminalsExtension.getExtensionContext().findTerminalBySerialNumber(terminalSerial.toString());
            if (isFresh(terminal)) {
                filteredTerminals.add(terminal);
            }
        });
        return filteredTerminals;
    }

    /**
     * Method filters terminals with long ping delay
     *
     * @param terminal
     * @return boolean
     */
    private boolean isFresh(ITerminal terminal) {
        long now = System.currentTimeMillis();
        if (terminal.getLastPingAt() != null
            && (terminal.getLastPingAt().getTime() + pingDelay) > now) { // 300000 ms = 5 min
            return true;
        }
        return false;
    }

}
