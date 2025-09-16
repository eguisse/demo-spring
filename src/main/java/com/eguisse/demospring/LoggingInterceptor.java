package com.eguisse.demospring;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component  // Registers this class as a Spring component
public class LoggingInterceptor implements HandlerInterceptor {

    // Logger for this class
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    // Logs HTTP method and URI before the request is handled
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        //logger.info("Request: {} {}", request.getMethod(), request.getRequestURI());
        MDC.put("requestId", request.getRequestId());
        MDC.put("requestURI", request.getRequestURI());
        MDC.put("correlationId", request.getHeader("X-Correlation-ID"));
        MDC.put("status","");
        return true;  // Allows the request to proceed
    }

    @Override
    // Logs response status and URI after request completion. Logs exceptions if any
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        //logger.info("Request: {} {}", request.getMethod(), request.getRequestURI());
        MDC.put("status", String.valueOf(response.getStatus()));
        if (ex != null) {
            // Logs any exception
            logger.error("Exception: ", ex);
        }
    }
}
