package com.kh.dotogether.member.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.dotogether.email.dto.EmailVerificationDTO;
import com.kh.dotogether.email.service.EmailService;
import com.kh.dotogether.member.model.dto.MemberDTO;
import com.kh.dotogether.member.model.dto.MemberIdResponseDTO;
import com.kh.dotogether.member.model.dto.PasswordUpdateDTO;
import com.kh.dotogether.member.model.service.MemberService;
import com.kh.dotogether.util.ResponseData;
import com.kh.dotogether.util.ResponseUtil;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/members")
public class MemberController {

	private final MemberService memberService;
	private final EmailService emailService;
	
	/**
	 * 회원가입 API
	 * @param memberDTO
	 * @return
	 */
	@PostMapping
	public ResponseEntity<ResponseData> signUp(@RequestBody @Valid MemberDTO memberDTO) {
		memberService.signUp(memberDTO);
		log.info("회원가입 성공: {}", memberDTO.getUserId());
		
		return ResponseEntity.ok(ResponseData.builder()
		            .code("200")
		            .message("회원가입에 성공했습니다.")
		            .items(Collections.emptyList())
		            .build()
		    );
	}
	
	/**
	 * 회원 탈퇴
	 * @param userNo
	 * @return
	 */
	@DeleteMapping("/{userNo}")
	public ResponseEntity<ResponseData> deleteUser(
					@PathVariable("userNo") Long userNo,
					@RequestHeader(value = "Authorization") String authorizationHeader) {
		
		memberService.deleteUser(userNo, authorizationHeader);
		
		return ResponseEntity.ok(ResponseData.builder()
		            .code("200")
		            .message("회원탈퇴가 정상적으로 처리되었습니다.")
		            .items(Collections.emptyList())
		            .build()
		    );
	}
	
	/**
     * 아이디 중복 확인
     * util > ResponseUtil
     */
    @GetMapping("/check-id/{userId}")
    public ResponseEntity<ResponseData> checkId(@PathVariable("userId") String userId) {
        boolean duplicated = memberService.isUserIdDuplicated(userId);
        return ResponseUtil.buildDuplicationResponse(duplicated, "아이디");
    }

    /**
     * 이메일 중복 확인
     */
    @GetMapping("/check-email/{email}")
    public ResponseEntity<ResponseData> checkEmail(@PathVariable("email") String email) {
        boolean duplicated = memberService.isEmailDuplicated(email);
        return ResponseUtil.buildDuplicationResponse(duplicated, "이메일");
    }

    /**
     * 연락처 중복 확인
     */
    @GetMapping("/check-phone/{phone}")
    public ResponseEntity<ResponseData> checkPhone(@PathVariable("phone") String phone) {
        boolean duplicated = memberService.isPhoneDuplicated(phone);
        return ResponseUtil.buildDuplicationResponse(duplicated, "연락처");
    }
    
    
    /**
     * 아이디 찾기
     * @param userName
     * @param userEmail
     * @return
     */
	@GetMapping("/find-id")
	public ResponseEntity<ResponseData> findUserId(
			@RequestParam("userName") String userName,
			@RequestParam(name="userEmail") String userEmail) {
		
		MemberIdResponseDTO result = memberService.findUserId(userName, userEmail);
		
		return ResponseEntity.ok(ResponseData.builder()
				.code("200")
				.message("아이디 조회 성공")
                .items(List.of(result))
                .build());
	}
	
	/**
	 * 비밀번호 찾기(1단계 - 아이디 조회)
	 * @param userId
	 * @return
	 */
	@GetMapping("/find-pw/step1/{userId}")
	public ResponseEntity<ResponseData> findPasswordStep1(@PathVariable("userId") String userId){
		log.info("비밀번호 찾기 1단계 - 아이디 확인 요청 : {}", userId);
		memberService.findByUserId(userId);
		
		return ResponseEntity.ok(ResponseData.builder()
				.code("200")
				.message("아이디 확인 성공")
				.items(List.of(Map.of("userId", userId)))
                .build());
	}
	
	/**
	 * 비밀번호 찾기(2단계 - 이메일 인증코드 요청보내기)
	 * @param userId
	 * @param userEmail
	 * @return
	 */
	@GetMapping("/find-pw/step2/{userId}")
	public ResponseEntity<ResponseData> findPasswordStep2(
	        @PathVariable("userId") String userId,
	        @RequestParam(name = "userEmail") @Email String userEmail) {
		
	    log.info("비밀번호 찾기 2단계 - 이메일 인증 요청: {}, {}", userId, userEmail);
	    return emailService.processPasswordResetVerification(userId, userEmail);
	}
	
	/**
	 * 비밀번호 찾기(2.5단계 - 이메일 인증코드 인증하기)
	 * @param dto
	 * @return
	 */
	@PostMapping("/verify-email")
	public ResponseEntity<ResponseData> verifyEmailCode(@Valid @RequestBody EmailVerificationDTO dto) {
		return emailService.verifyEmailCode(dto.getEmail(), dto.getCode());
	}
	
	/**
	 * 비밀번호 찾기(3단계 - 새 비밀번호 설정)
	 * @param userId
	 * @param dto
	 * @return
	 */
	@PutMapping("/find-pw/step3/{userId}")
	public ResponseEntity<ResponseData> resetPasswordStep3(
			@PathVariable("userId") String userId, @RequestBody PasswordUpdateDTO dto) {
	    
		memberService.resetPassword(userId, dto.getUserEmail(), dto.getUserPw());
	    return ResponseEntity.ok(
    	    new ResponseData("", "비밀번호가 성공적으로 변경되었습니다.", Collections.emptyList())
    	);
	}
	
	
	
	
	
	

}
