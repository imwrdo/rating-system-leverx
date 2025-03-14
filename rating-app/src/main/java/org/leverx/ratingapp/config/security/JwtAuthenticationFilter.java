package org.leverx.ratingapp.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.leverx.ratingapp.services.auth.jwt.JwtServiceImplementation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * JWT authentication filter that intercepts incoming requests to validate and authenticate JWT tokens.
 * This filter ensures that users accessing protected resources have a valid authentication token.
 */
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtServiceImplementation jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Filters incoming HTTP requests to authenticate users based on JWT tokens.
     * Extracts the JWT token from the Authorization header, validates it, and sets the authentication context.
     *
     * @param request     The incoming HTTP request.
     * @param response    The HTTP response.
     * @param filterChain The filter chain to continue processing other filters.
     * @throws ServletException If an error occurs during request processing.
     * @throws IOException      If an I/O error occurs.
     */

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        // Retrieve the Authorization header from the request
        final String authorizationHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        // Check if the header is present and follows the "Bearer " format
        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract the JWT token by removing the "Bearer " prefix
        jwt = authorizationHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        // If the user is not yet authenticated and a username is extracted, proceed with validation
        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            UserDetails user = this.userDetailsService.loadUserByUsername(userEmail);

            // Validate the JWT token and set authentication if valid
            if(jwtService.isTokenValid(jwt, user)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()
                );
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set authentication in the security context
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}
