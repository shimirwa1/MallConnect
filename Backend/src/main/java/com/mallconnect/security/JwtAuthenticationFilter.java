package com.mallconnect.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7).trim();
        final String userEmail;

        // Debug: print exact token with brackets to spot extra chars
        System.out.println("JWT = [" + jwt + "]");
        System.out.println("JWT length = " + jwt.length());
        // Print the first and last 10 characters as char codes to spot invisible chars
        if (jwt.length() > 10) {
            System.out.print("JWT first 10 chars: ");
            jwt.substring(0, 10).chars().forEach(c -> System.out.print(c + " "));
            System.out.println();
            System.out.print("JWT last 10 chars: ");
            jwt.substring(jwt.length() - 10).chars().forEach(c -> System.out.print(c + " "));
            System.out.println();
        }
        long dotCount = jwt.chars().filter(ch -> ch == '.').count();
        log.debug("JWT received - length: {}, dots: {} (expecting 2 dots for header.payload.signature)",
                jwt.length(), dotCount);

        try {
            userEmail = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // Token is invalid (expired, bad signature, etc.) — reject with 401
            log.warn("JWT extraction failed: {} - Token (first 50 chars): {}", e.getMessage(),
                    jwt.length() > 50 ? jwt.substring(0, 50) + "..." : jwt);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid or expired token\"}");
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                // Token is not valid (expired or user mismatch)
                log.warn("JWT validation failed for user: {}", userEmail);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Token is expired or invalid\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
