package com.example.jwt_auth_api.Config;

import com.example.jwt_auth_api.Model.User;
import com.example.jwt_auth_api.Repository.UserRepository;
import com.example.jwt_auth_api.Util.JWTUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public JWTAuthenticationFilter(JWTUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith(token)) {
            token = authHeader.substring(7);
            try {
                email = jwtUtil.getEmailFromToken(token);
            } catch (JwtException ex) {
                logger.debug("Invalid JWT: " + ex.getMessage());
            } catch (Exception ex) {
                logger.debug("Error parsing JWT: " + ex.getMessage());
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<User> maybeUser = userRepository.findByEmail(email);
            if (maybeUser.isPresent() && token != null && jwtUtil.validateToken(token)) {
                User user = maybeUser.get();

                // map role (e.g. "USER" or "ROLE_USER") to SimpleGrantedAuthority
                String role = user.getRole();
                // ensure it has ROLE_ prefix expected by Spring if needed
                String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(authority));

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
