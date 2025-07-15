package com.kh.dotogether.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kh.dotogether.configuration.filter.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfigure {
	
	private final JwtFilter jwtFilter;
	@Value("${deploy.publicIp}")
	private String publicIp;


   /*   Argon2PasswordEncoder
    *   파라미터   설명   추천값
      saltLength   솔트 길이 (byte)   보통 16
      hashLength   해시 길이 (byte)   보통 32
      parallelism   병렬 처리 수   1~4 (보통 CPU 코어 수)
      memory   메모리 사용량 (KB 단위)   65536 이상 추천 (64MB)
      iterations   반복 횟수   2 이상 추천
    */
   @Bean
   public PasswordEncoder passwordEncoder() {
       return new Argon2PasswordEncoder(
               16, // saltLength
               32, // hashLength
               1,  // parallelism
               65536, // memoryInKb → 64MB
               3   // iterations
       );
   }
   
   @Bean
   public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
      return config.getAuthenticationManager();
   }
   
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .formLogin(config -> config.disable())
            .httpBasic(config -> config.disable())
            .csrf(config -> config.disable())
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth

                // 인증 없이 허용되는 경로
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/refresh").permitAll()
                .requestMatchers("/api/auth/logout").permitAll()
                .requestMatchers("/api/members/**").permitAll()
                .requestMatchers("/api/members/check-id/**").permitAll()
                .requestMatchers("/api/members/check-email/**").permitAll()
                .requestMatchers("/api/members/check-phone/**").permitAll()
                .requestMatchers("/api/members/find-pw/**").permitAll()                
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/teams").permitAll()
                // 관리자 권한
                .requestMatchers("/api/admin/**").authenticated()


             // 로그인 필요
                .requestMatchers(HttpMethod.DELETE, "/api/members/{id}",
                									"/api/teams/join-cancle",
                									"/api/teams",
                									"/api/teams/**",
                									"/api/section",
                									"/api/section/**",
                									"/api/schedule",
                									"/api/schedule/**",
                									"/api/challenge/comment/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/teams",
                								  "/api/teams/join",
                								  "/api/teams/join-accept",
                								  "/api/challenge",
                								  "/api/challenge/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/challenge/**",
                								 "/api/challenge/comment/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/teams/**",
                								 "/api/chat",
                								 "/api/challenge",
                								 "/api/challenge/**",
              									  "/api/section",
              									  "/api/section/**",
              									  "/api/schedule",
            									  "/api/schedule/**",
            									  "/api/works/**").authenticated()
                .requestMatchers("/api/info/**").authenticated()
                .requestMatchers("/api/profile/**").authenticated()
                
                // 정적 자원
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 배포 시 수정 부분
//        configuration.setAllowedOrigins(Arrays.asList(publicIp));
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        //configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
   
}
