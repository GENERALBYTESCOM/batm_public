package com.generalbytes.batm.server.extensions.extra.examples.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

public class ServletFilterExample implements Filter {

    private static final Logger log = LoggerFactory.getLogger("batm.master.extensions.ServletFilterExample");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        log.debug("Server info: " + httpRequest.getSession().getServletContext().getServerInfo());
        log.debug("Request uri: " + httpRequest.getRequestURI());
        log.debug("Request headers:");
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.debug("  " + headerName + " - " + httpRequest.getHeader(headerName));
        }

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.addHeader("X-Powered-By", "GB");

        chain.doFilter(httpRequest, httpResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No init necessary.
    }

    @Override
    public void destroy() {
        // No state to clean up.
    }
}
