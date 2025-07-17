package com.kh.dotogether.member.model.service;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.dotogether.auth.util.EncryptionUtil;
import com.kh.dotogether.auth.util.JWTUtil;
import com.kh.dotogether.exception.exceptions.CustomException;
import com.kh.dotogether.global.enums.ErrorCode;
import com.kh.dotogether.log.model.service.LogService;
import com.kh.dotogether.member.model.dao.MemberMapper;
import com.kh.dotogether.member.model.dto.MemberDTO;
import com.kh.dotogether.member.model.dto.MemberIdResponseDTO;
import com.kh.dotogether.password.service.PasswordService;
import com.kh.dotogether.token.model.service.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberMapper memberMapper;
	private final PasswordService passwordService; // 비밀번호 암호화(Argon2)
	private final TokenService tokenService;
	private final EncryptionUtil encryptionUtil; // 양방향 암호화
	private final JWTUtil jwtUtil;
	private final LogService logService;

	/**
	 * 회원가입
	 */
	@Override
	public void signUp(MemberDTO memberDTO) {
		// 중복 체크
		if(isUserIdDuplicated(memberDTO.getUserId())) {
			throw new CustomException(ErrorCode.DUPLICATE_USER_ID);
		}
		if(isEmailDuplicated(memberDTO.getUserEmail())) {
			throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
		}
		if(isPhoneDuplicated(memberDTO.getUserId())) {
			throw new CustomException(ErrorCode.DUPLICATE_PHONE);
		}
		
		// 패스워드 암호화(일방향)
		memberDTO.setUserPw(passwordService.encodePassword(memberDTO.getUserPw()));
		
		// 연락처, 이메일 암호화 저장(양방향)
		memberDTO.setUserPhone(encryptionUtil.encrypt(memberDTO.getUserPhone()));
		memberDTO.setUserEmail(encryptionUtil.encrypt(memberDTO.getUserEmail()));
		
		// 기본값 설정
		memberDTO.setUserRole("USER");
		memberDTO.setUserStatus("Y");
		
		// 회원가입 진행
		memberMapper.signUp(memberDTO);
		
		log.info("회원가입 완료: userId = {}", memberDTO.getUserId());
		
		logService.insertLog(memberDTO.getUserId(), memberDTO.getUserName(), "회원가입");
	}

	/**
	 * 아이디, 이메일, 연락처 중복 체크
	 */
	@Override
	public boolean isUserIdDuplicated(String userId) {
		return memberMapper.existsByUserId(userId) > 0;
	}
	@Override
	public boolean isEmailDuplicated(String email) {
		String encEmail = encryptionUtil.encrypt(email);
		return memberMapper.existsByEmail(encEmail) > 0;
	}
	@Override
	public boolean isPhoneDuplicated(String phone) {
		String encPhone = encryptionUtil.encrypt(phone);
		return memberMapper.existsByPhone(encPhone) > 0;
	}

	/**
	 * 회원 탈퇴(토큰 삭제)
	 */
	@Override
	@Transactional
	public boolean deleteUser(Long userNo, String authorizationHeader) {
		// 토큰 유효성 검사 및 사용자 인증
		String token = extractToken(authorizationHeader);
		String userIdFromToken = jwtUtil.getUserIdFromToken(token);
		
		MemberDTO member = getValidMember(userNo);
		
		if(!member.getUserId().equals(userIdFromToken)) {
			throw new CustomException(ErrorCode.ONLY_SELF_DELETE);
		}
		
		// userStatus = 'N'으로 변경
		int result = memberMapper.deleteUser(member.getUserNo());
		
		// 리프레시 토큰 삭제
		tokenService.deleteUserToken(userNo);
		log.info("회원탈퇴 완료: userId = {}", userNo);
		
		logService.insertLog(member.getUserId(), member.getUserName(), "회원탈퇴");
		
		return result > 0;
	}

	
	/**
	 * 아이디 찾기(이름, 이메일)
	 */
	@Override
	public MemberIdResponseDTO findUserId(String userName, String userEmail) {
		List<MemberDTO> candidates = memberMapper.findByName(userName);

	    for (MemberDTO member : candidates) {
	        String decrypted = encryptionUtil.decrypt(member.getUserEmail());
	        if (userEmail.equals(decrypted)) {
	            log.info("아이디 찾기 성공 - userId: {}", member.getUserId());
	            return new MemberIdResponseDTO(member.getUserId());
	        }
	    }

	    throw new CustomException(ErrorCode.NOT_FOUND_USER);
	}

	/**
	 * 비밀번호 찾기 1단계 - 아이디 조회
	 * 2단계는 emailService
	 */
	@Override
	public MemberDTO findByUserId(String userId) {
		MemberDTO member = memberMapper.findByUserId(userId);

	    if (member == null) {
	        throw new CustomException(ErrorCode.NOT_FOUND_USER);
	    }
		return memberMapper.findByUserId(userId);
	}
	
	/**
	 * 비밀번호 찾기 3단계 - 새 비밀번호 설정
	 */
	@Override
	public void resetPassword(String userId, String email, String newPassword) {
		MemberDTO member = findByUserId(userId);
	    validateEmailMatches(member, email);
	    updatePassword(member.getUserNo(), newPassword);
	    
		log.info("비밀번호 재설정 완료: userId = {}, email = {}", userId, email);
	}
	
	
	@Override
	public MemberDTO findByUserNo(Long userNo) {
		MemberDTO member = getValidMember(userNo);
		
		// 연락처, 이메일 복호화
		member.setUserPhone(encryptionUtil.decrypt(member.getUserPhone()));
		member.setUserEmail(encryptionUtil.decrypt(member.getUserEmail()));
		
		return member;
	}

	/**
	 * 사용자 확인
	 * @param userNo
	 * @return
	 */
	private MemberDTO getValidMember(Long userNo) {
	    MemberDTO member = memberMapper.findByUserNo(userNo);
	    if (member == null) {
	        throw new CustomException(ErrorCode.NOT_FOUND_USER);
	    }
	    return member;
	}
	
	/**
	 * 토큰 확인
	 * @param authorizationHeader
	 * @return
	 */
	private String extractToken(String authorizationHeader) {
		if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			throw new CustomException(ErrorCode.INVALID_AUTH_INFO);
		}
		return authorizationHeader.substring(7); // "Bearer " 제거
	}
	
	
	/**
	 * 이메일 일치 검사 
	 * @param member
	 * @param plainEmail
	 */
	private void validateEmailMatches(MemberDTO member, String plainEmail) {
	    String decryptedEmail = encryptionUtil.decrypt(member.getUserEmail());
	    log.info("넘어온 이메일: {}", plainEmail);
	    log.info("DB 복호화 이메일: {}", decryptedEmail);

	    if (!plainEmail.equals(decryptedEmail)) {
	        throw new CustomException(ErrorCode.EMAIL_NOT_FOUND);
	    }
	}
	
	/**
	 * 비밀번호 일치 검사
	 * @param userNo
	 * @param rawPassword
	 */
	private void updatePassword(Long userNo, String rawPassword) {
	    String encodedPassword = passwordService.encodePassword(rawPassword);
	    int result = memberMapper.updatePassword(userNo, encodedPassword);
	    if (result != 1) {
	        throw new CustomException(ErrorCode.USER_UPDATE_FAILED);
	    }
	}

	@Override
    public List<MemberDTO> findAll(int page, int size) {
        RowBounds rowBounds = new RowBounds((page - 1) * size, size);
        List<MemberDTO> members = memberMapper.findAll(rowBounds);
        // 이메일, 연락처 복호화
        for (MemberDTO member : members) {
            member.setUserEmail(encryptionUtil.decrypt(member.getUserEmail()));
            member.setUserPhone(encryptionUtil.decrypt(member.getUserPhone()));
        }

        return members;
    }

    @Override
    public int countAll() {
        return memberMapper.countAll();
    }

	@Override
	public boolean updateUserStatus(String userId, String newStatus) {
		log.info("updateUserStatus 호출: userId={}, newStatus={}", userId, newStatus);
	    int result = memberMapper.updateUserStatus(userId, newStatus);
	    log.info("updateUserStatus 결과: result={}", result);
	    return result == 1;
	}

}
