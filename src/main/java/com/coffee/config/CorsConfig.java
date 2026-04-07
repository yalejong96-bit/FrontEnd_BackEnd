package com.coffee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {
    @Bean // Spring Security가 Bean을 읽어서 CORS 정책으로 사용합니다.
    public CorsConfigurationSource corsConfigurationSource(){
        // configuration 객체는 클라이언트로부터 요청이 들어 왔을 때 CORS 정책을 적응시켜주는 객체입니다.
        CorsConfiguration configuration = new CorsConfiguration();

        // 리엑트의 포트 번호를 여기에 작성
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://127.0.0.1:5173"
        ));

        // 허용 HTTP 메소드
        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        //Authorizationionsms는 axiosInstance.tsx 파일 참조
        //Content-Type는 LoginPage.tsx 파일 참조
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept"
        ));

        // 쿠키 Authorization 헤더 포함 요청 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source
                = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source ;
    }
}
