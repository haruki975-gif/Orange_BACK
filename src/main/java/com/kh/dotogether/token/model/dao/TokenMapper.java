package com.kh.dotogether.token.model.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.dotogether.token.vo.RefreshToken;

@Mapper
public interface TokenMapper {
	
	// 새 리프레시 토큰 저장
	// RefreshToken 객체의 필드를 사용하므로, XML에선 token.필드명 형식으로 접근
	void saveToken(@Param("token") RefreshToken token);

	// 리프레시 토큰으로 조회
	RefreshToken findByToken(@Param("token") String token);
	
	// 유저별 리프레시 토큰 삭제
	void deleteTokenByUserNo(@Param("userNo") Long userNo);
	
	// 만료된 리프레시 토큰 삭제
	void deleteExpiredRefreshToken(@Param("currentTime") Date currentTime);
}
