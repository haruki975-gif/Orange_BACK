package com.kh.dotogether.exception;


import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.kh.dotogether.exception.exceptions.CustomException;
import com.kh.dotogether.util.ResponseError;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private ResponseEntity<ResponseError> exceptionHandler(String code, String message){
		
		ResponseError responseError = ResponseError.builder().code(code)
							   								 .message(message)
							   								 .build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
	}
	
	// 로직실행 중 발생하는 예외 처리
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ResponseError> handleCustomException(CustomException e){
		
		return exceptionHandler(e.getCode(), e.getMessage());
	}
	
	// @valid 유효성 검증에 실패했을 경우 발생하는 예외 처리
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleArgumentNotValid(MethodArgumentNotValidException e) {
		
		Map<String, String> errors = new HashMap<>();
		
		e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		
		ResponseError responseError = ResponseError.builder().code("106")
															 .messages(errors)
															 .build();
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseError);
	}
	
}
