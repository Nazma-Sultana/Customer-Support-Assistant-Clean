package com.example.demo.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: read the Authorization header from the request
        String authHeader = request.getHeader("Authorization");

        // Step 2: check if header exists and starts with "Bearer "
        // If not, skip this filter and move on — request may be public
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: extract the token by removing "Bearer " prefix (7 characters)
        String token = authHeader.substring(7);

        // Step 4: validate the token — checks signature and expiry
        if (!jwtUtils.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 5: extract username from the token payload
        String username = jwtUtils.getUsernameFromToken(token);

        // Step 6: load full user details from DB using the username
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Step 7: create an authenticated token with user details and roles
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,       // principal (who is logged in)
                        null,              // credentials (not needed — already verified by JWT)
                        userDetails.getAuthorities() // roles e.g. [ROLE_CUSTOMER]
                );

        // Step 8: attach request details (IP, session) to the authentication token
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Step 9: store authentication in SecurityContext — marks this request as authenticated
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Step 10: continue to the next filter or the controller
        filterChain.doFilter(request, response);
    }
}
