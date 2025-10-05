package com.blackcode.financial_evaluations_service.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class InternalRequestInterceptor implements HandlerInterceptor {

    @Value("${internal.api.secret}")
    private String internalSecret;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String headerSecret = request.getHeader("X-Internal-Token");

        if (!internalSecret.equals(headerSecret)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Forbidden: Invalid or missing internal token");
            return false;
        }

        return true;
    }


}
