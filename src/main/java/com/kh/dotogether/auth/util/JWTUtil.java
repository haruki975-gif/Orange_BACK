package com.kh.dotogether.auth.util;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JWTUtil {
	
	@Value("${jwt.secret}")
	private String secretKey;
	private SecretKey key;
	
	// 엑세스 토큰 시간
	//private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 120; // 2시간
	private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 30; // 30분
	
	@PostConstruct
	public void init() {
		byte[] keyArr = Base64.getDecoder().decode(secretKey);
		this.key = Keys.hmacShaKeyFor(keyArr);
		log.info("JWT 시크릿 키 초기화 완료되었습니다.");
	}
	
	/**
	 * 액세스 토큰 생성 (30분 유효)
	 * 테스트 - 2시간 유효
	 */
	public String getAccessToken(String userId, String role) {
		return Jwts.builder()
				   .setSubject(userId)
				   .claim("role", role)
				   .setIssuedAt(new Date())
				   .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
				   .signWith(key)
				   .compact();
	}
	
	/**
	 * 리프레시 토큰 생성 (30일 유효)
	 */
	public String getRefreshToken(String userId, String role) {
		return Jwts.builder()
				   .setSubject(userId)
                   .claim("role", role)
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(System.currentTimeMillis() + 2592000000L)) // 30일
                   .signWith(key)
                   .compact();
	}
	
	/**
	 * JWT 토큰 파싱 및 검증
	 * 파싱: 복잡한 데이터 → 쓸 수 있는 구조로 바꾸기
	 * JWT 문자열 → 사용자 정보, 권한, 만료일 등을 담은 Claims 객체로 바꾸는 과정
	 * 파싱하면 객체로 바뀜
	 * {
		  "sub": "noeul123",
		  "role": "USER",
		  "exp": 1721224080
		}
	 */
	public Claims parseJwt(String token) {
	    return Jwts.parserBuilder()
	               .setSigningKey(key)
	               .build()
	               .parseClaimsJws(token)
	               .getBody();
	}
	
	/**
     * 토큰 만료 시간 조회
     */
    public Long getExpirationTime(String token) {
        Claims claims = parseJwt(token);
        return claims.getExpiration().getTime();
    }

    /**
     * 토큰에서 사용자 ID 추출
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseJwt(token);
        return claims.getSubject();
    }

    /**
     * 토큰에서 사용자 역할 추출
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseJwt(token);
        return claims.get("role", String.class);
    }
}
