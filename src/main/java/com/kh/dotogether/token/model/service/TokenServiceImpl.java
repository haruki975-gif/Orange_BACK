package com.kh.dotogether.token.model.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.dotogether.auth.util.JWTUtil;
import com.kh.dotogether.exception.exceptions.CustomException;
import com.kh.dotogether.global.enums.ErrorCode;
import com.kh.dotogether.member.model.dao.MemberMapper;
import com.kh.dotogether.member.model.dto.MemberDTO;
import com.kh.dotogether.token.model.dao.TokenMapper;
import com.kh.dotogether.token.vo.RefreshToken;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JWTUtil jwtUtil;
    private final TokenMapper tokenMapper;
    private final MemberMapper memberMapper;

    @Override
    @Transactional
    public Map<String, String> generateToken(String userId, String role) {
        // 회원 상태 확인
        MemberDTO member = memberMapper.findByUserId(userId);
        if (member == null || "N".equals(member.getUserStatus())) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        // 액세스/리프레시 토큰 생성
        Map<String, String> tokens = createTokens(userId, role);

        // 기존 리프레시 토큰 삭제
        tokenMapper.deleteTokenByUserNo(member.getUserNo());

        // 새 리프레시 토큰 저장
        saveRefreshToken(tokens.get("refreshToken"), member.getUserNo());

        // 만료된 리프레시 토큰 삭제(현재 로그인과 직접 관련 없는 전체 토큰 정리 작업)
        tokenMapper.deleteExpiredRefreshToken(new Date());

        return tokens;
    }

    /**
     * 리프레시 토큰 DB 저장
     */
    private void saveRefreshToken(String refreshToken, Long userNo) {
        Date expireAt = new Date(System.currentTimeMillis() + (3600000L * 24 * 30)); // 30일

        RefreshToken token = RefreshToken.builder()
                .userNo(userNo)
                .token(refreshToken)
                .expireAt(expireAt)
                .build();

        tokenMapper.saveToken(token);
        log.info("리프레시 토큰 저장 완료: userNo = {}", userNo);
    }

    /**
     * 액세스/리프레시 토큰 생성
     */
    private Map<String, String> createTokens(String userId, String role) {
        String accessToken = jwtUtil.getAccessToken(userId, role);
        String refreshToken = jwtUtil.getRefreshToken(userId, role);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    @Override
    @Transactional
    public Map<String, String> refreshToken(String refreshToken) {
        try {
            // 리프레시 토큰 파싱
            Claims claims = jwtUtil.parseJwt(refreshToken);
            String userId = claims.getSubject();

            // 회원 상태 확인
            MemberDTO member = memberMapper.findByUserId(userId);
            if (member == null || "N".equals(member.getUserStatus())) {
            	throw new CustomException(ErrorCode.NOT_FOUND_USER);
            }

            // 리프레시 토큰 DB 조회
            RefreshToken tokenEntity = tokenMapper.findByToken(refreshToken);
            if (tokenEntity == null) {
            	throw new CustomException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }

            // 만료 확인
            if (tokenEntity.getExpireAt().before(new Date())) {
                tokenMapper.deleteTokenByUserNo(member.getUserNo());
                throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
            }

            // 새 토큰 발급
            Map<String, String> tokens = createTokens(member.getUserId(), member.getUserRole());
            return tokens;

        } catch (ExpiredJwtException e) {
        	log.warn("리프레시 토큰 만료: {}", e.getMessage());
        	throw new CustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
        } catch (Exception e) {
        	log.warn("리프레시 토큰 처리 중 예외 발생: {}", e.getMessage());
        	throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    @Override
    @Transactional
    public void deleteUserToken(Long userNo) {
        tokenMapper.deleteTokenByUserNo(userNo);
        log.info("사용자 userNo 삭제: {}", userNo);
    }
}
