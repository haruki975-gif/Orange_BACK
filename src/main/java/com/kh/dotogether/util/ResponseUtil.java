package com.kh.dotogether.util;

import java.util.Collections;

import org.springframework.http.ResponseEntity;

// 아이디, 이메일, 연락처 중복 처리
public class ResponseUtil {

	public static ResponseEntity<ResponseData> buildDuplicationResponse(boolean duplicated, String fieldName){
		String code = duplicated ? "409" : "200";
		String message = duplicated
				? "이미 사용중인 " + fieldName + "입니다."
				: "사용 가능한 " + fieldName + "입니다.";
		
		return ResponseEntity.ok(ResponseData.builder()
				.code(code)
				.message(message)
				.items(Collections.emptyList())
				.build());
	}
}
