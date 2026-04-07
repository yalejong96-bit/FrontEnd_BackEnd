package com.coffee.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider ;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override // 요청이 들어올 때 마다 컨트롤러에 앞서서 먼저 실행이 되는 메소드입니다.
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String bearer = request.getHeader("Authorization");

        if(bearer != null && bearer.startsWith("Bearer ")){
            String token = bearer.substring("Bearer ".length());

            if(jwtTokenProvider.validateToken(token)){
                String email = jwtTokenProvider.getEmail(token);
                Claims claims = jwtTokenProvider.getClaims(token);
                String role = claims.get("role", String.class);

                // 권한 객체 생성
                List<GrantedAuthority> authorities
                        = List.of(new SimpleGrantedAuthority("ROLE_" + role));

                // 인증 객체 생성
                UsernamePasswordAuthenticationToken auth
                        = new UsernamePasswordAuthenticationToken(email, null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
