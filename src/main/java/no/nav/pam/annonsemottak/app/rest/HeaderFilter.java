package no.nav.pam.annonsemottak.app.rest;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("X-Frame-Options", "SAME");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("cache-control", "no-cache, no-store, must-revalidate");
        response.setHeader("pragma", "no-cache");
        response.setHeader("expires", "0");
        filterChain.doFilter(request, response);
    }
}
