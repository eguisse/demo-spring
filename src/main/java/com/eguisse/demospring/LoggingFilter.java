package com.eguisse.demospring;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;

@Component  // Registers this filter as a Spring component.
public class LoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);  // Logger instance for logging.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Log request details before processing the request.
        logger.debug("Request: Method={}, URI={}, Headers={}",
                request.getMethod(),
                request.getRequestURI(),
                getRequestHeaders(request));

        long startTime = System.currentTimeMillis();  // Capture the start time to measure processing duration.

        try {
            filterChain.doFilter(request, response);  // Continue with the next filter in the chain.
        } finally {
            long duration = System.currentTimeMillis() - startTime;  // Calculate how long the request took.
            MDC.put("duration-ms", String.valueOf(duration));

            // Log response details after request processing.
            logger.info("Response: Method={}, Status={}, URI={}, Duration={}ms",
                    request.getMethod(),
                    response.getStatus(),
                    request.getRequestURI(),
                    duration);
            // Log any exceptions that occurred during processing.
            if (request.getAttribute("javax.servlet.error.exception") != null) {
                logger.error("Exception during request processing",
                        (Exception) request.getAttribute("javax.servlet.error.exception"));
            }
        }
    }

    // Utility method to extract request headers for logging.

    /**
     * Extracts and logs all headers from the incoming HTTP request.
     * @param request
     * @return
     */
    private String getRequestHeaders(HttpServletRequest request) {
        Iterator<String> i = request.getHeaderNames().asIterator();
        Dictionary<String,String> headers = new Hashtable<>();
        while (i.hasNext()) {
            String header = (String) i.next();
            // add attribute to log
            headers.put(header, request.getHeader(header));
            logger.atInfo().addKeyValue(header, request.getHeader(header));
        }
        logger.atInfo().addKeyValue("remoteAddr", request.getRemoteAddr());
        return headers.toString();
    }

    /**
     * Extract response headers for logging.
     * @param response
     * @return String representation of response headers.
     */
    private String getResponseHeaders(HttpServletResponse response) {
        Iterator<String> i = response.getHeaderNames().iterator();
        Dictionary<String,String> headers = new Hashtable<>();
        while (i.hasNext()) {
            String header = (String) i.next();
            headers.put(header, response.getHeader(header));
            logger.atInfo().addKeyValue(header, response.getHeader(header));
        }
        return headers.toString();
    }

}
