package org.blogs.Blogs.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.blogs.Blogs.service.CustomUserService;
import org.blogs.Blogs.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;
    private final CustomUserService service;
    Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String header = request.getHeader("Authorization");

        String jwtToken =null;
        String email = null;

        try{
            if (header != null && header.startsWith("Bearer ")) {
                jwtToken = header.substring(7);
                logger.info("Token Received");

                try {
                    email = jwt.extractUsername(jwtToken);
                    logger.info("this is email " + email);
                } catch (ExpiredJwtException e) {
                    logger.warn("Token expired" + e.getMessage());
                } catch (RuntimeException e) {
                    logger.error("Invalid token" + e.getMessage());
                }
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails details = service.loadUserByUsername(email);
                if (jwt.validate(jwtToken, details)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }catch (Exception e) {
            logger.warn("JWT: Relative Error: "+ e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getStatus();
            response.getWriter().write(
                    "{\"error\": \"Invalid or expired token: \"" +
                            "\"message\":\""+e.getMessage()+"\"}"
            );
            return;
        }
        filterChain.doFilter(request, response);
    }
}
