package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // Check if header starts with Bearer
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7); // remove "Bearer "
            try {
                email = jwtUtil.extractEmail(jwt);
            } catch (Exception e) {
                System.out.println("Invalid JWT: " + e.getMessage());
            }
        }

        // If we got email and SecurityContext has no authentication yet
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Here, we don’t load from DB (minimal example), just create a dummy user
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            new User(email, "", Collections.emptyList()),  // Spring Security User
                            null,
                            Collections.emptyList()
                    );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Put authentication into security context
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
