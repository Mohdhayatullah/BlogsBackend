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
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwt;
    private final CustomUserService service;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        String header = request.getHeader("Authorization");

        String jwtToken =null;
        String email = null;

        if (header != null && header.startsWith("Bearer ")){
            jwtToken = header.substring(7);
            logger.info("THis token come from UI "+jwtToken);

            try{
                email = jwt.extractUsername(jwtToken);
                logger.info("this is email "+email);
            }catch (ExpiredJwtException e){
                logger.warn("internal error, maybe something wrong "+ e.getMessage());
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails details = service.loadUserByUsername(email);
            if (jwt.validate(jwtToken, details)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request,response);
    }
}
