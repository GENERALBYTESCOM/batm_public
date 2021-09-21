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
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

@Path("/terminals")
public class RestServiceActiveTerminals {
    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.activeTerminals.ActiveTerminalsExtension");
    private static long pingDelay = 1000 * 60 * 5;

    /*
     * https://localhost:7743/extensions/example/active/terminals
     * Returns list of active terminals ( active = pingDelay < 5 min ).
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Object terminals(@QueryParam("api_key") String apiKey) {
        IApiAccess iApiAccess = ActiveTerminalsExtension.getExtensionContext().getAPIAccessByKey(apiKey);
        if (iApiAccess != null) {
            return getTerminalsByApiKey(iApiAccess);
        }
        return Response.status(HttpServletResponse.SC_UNAUTHORIZED).entity("Unauthorized response").build();
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
            ITerminal terminal = ActiveTerminalsExtension.getExtensionContext().findTerminalBySerialNumber(terminalSerial);
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

}
