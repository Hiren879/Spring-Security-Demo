package com.demo.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer";
    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtilService jwtUtilService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 1. Get the authorization header
        String authHeader = request.getHeader(AUTHORIZATION);

        String token = null;
        String userName = null;

        // 2. Get the user from the token if token is available
        if (Objects.nonNull(authHeader) && authHeader.startsWith(BEARER)) {
            token = authHeader.substring(7);
            userName = jwtUtilService.getUserNameFromToken(token);
        } else {
            logger.error("Invalid token.");
        }

        // 3. If user is found and securityContext is NULL then
        if (Objects.nonNull(userName) && Objects.isNull(SecurityContextHolder.getContext()
                .getAuthentication())) {
            // 3.1 Fetch the actual user from your DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            // 3.2 Validate the token against UserDetails and set the context
            if (jwtUtilService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext()
                        .setAuthentication(usernamePasswordAuthentication);
            } else {
                logger.info("Validation fails !!");
            }
        }
        filterChain.doFilter(request, response);
    }
}
