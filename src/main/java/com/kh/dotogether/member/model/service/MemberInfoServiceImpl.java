package com.kh.dotogether.member.model.service;

import org.springframework.stereotype.Service;
import com.kh.dotogether.auth.util.EncryptionUtil;
import com.kh.dotogether.exception.exceptions.CustomException;
import com.kh.dotogether.global.enums.ErrorCode;
import com.kh.dotogether.log.model.service.LogService;
import com.kh.dotogether.member.model.dao.MemberInfoMapper;
import com.kh.dotogether.member.model.dao.MemberMapper;
import com.kh.dotogether.member.model.dto.MemberAddressDTO;
import com.kh.dotogether.member.model.dto.MemberDTO;
import com.kh.dotogether.member.model.dto.MemberInfoUpdateDTO;
import com.kh.dotogether.member.model.dto.MypagePasswordUpdateDTO;
import com.kh.dotogether.password.service.PasswordService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberInfoServiceImpl implements MemberInfoService {

    private final EncryptionUtil encryptionUtil;
	private final MemberMapper memberMapper;
	private final PasswordService passwordService;
	private final MemberInfoMapper memberInfoMapper;
	private final LogService logService;

	@Override
	public String findUserPassword(Long userNo, String password) {
		
		// 입력한 비밀번호 유효성 검사
		if(password == null || password.trim().isEmpty()) {
			throw new CustomException(ErrorCode.INVALID_LOGIN_INFO);
		}
		
		// 사용자 정보 조회
		MemberDTO member = getValidMember(userNo);
		
		// 암호화된 비밀번호 비교
		String encryptedPassword = member.getUserPw();
		if(encryptedPassword == null || encryptedPassword.isBlank()) {
			throw new CustomException(ErrorCode.INVALID_LOGIN_INFO);
		}
		
		boolean isMatch = passwordService.matches(password, encryptedPassword);
		return isMatch ? "비밀번호가 일치합니다." : "비밀번호가 일치하지 않습니다.";
	}

	
	@Override
	public void updateUserPassword(Long userNo, @Valid MypagePasswordUpdateDTO dto) {
		MemberDTO member = getValidMember(userNo);
		
		// 현재 비밀번호 일치 여부 확인
	    if (!passwordService.matches(dto.getUserPassword(), member.getUserPw())) {
	        throw new CustomException(ErrorCode.INVALID_LOGIN_INFO);
	    }
	    
	    // 새 비밀번호 암호화 후 저장
	    String encryptedPassword = passwordService.encodePassword(dto.getNewPassword());
	    int result = memberMapper.updatePassword(userNo, encryptedPassword);
	    
	    if (result == 0) {
	    	throw new CustomException(ErrorCode.USER_UPDATE_FAILED);
	    }
	    
		logService.insertLog(member.getUserId(), member.getUserName(), "회원 비밀번호 변경");
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
	 * 개인정보수정 - 연락처 조회
	 */
	@Override
	public String findUserPhone(Long userNo) {
		MemberDTO member = getValidMember(userNo);
	    try {
	        return encryptionUtil.decrypt(member.getUserPhone());
	    } catch (Exception e) {
	        throw new CustomException(ErrorCode.DECRYPTION_FAILED);
	    }
	}

	/**
	 * 개인정보수정 - 이메일 조회
	 */
	@Override
	public String findUserEmail(Long userNo) {
		MemberDTO member = getValidMember(userNo);
	    try {
	        return encryptionUtil.decrypt(member.getUserEmail());
	    } catch (Exception e) {
	        throw new CustomException(ErrorCode.DECRYPTION_FAILED);
	    }
	}


	/**
	 * 개인정보수정 - 주소 조회
	 */
	@Override
	public MemberAddressDTO findUserAddress(Long userNo) {
		getValidMember(userNo);
		MemberAddressDTO addressDTO = memberInfoMapper.findUserAddress(userNo);
		
	    if (addressDTO == null || (addressDTO.getUserAddress1() == null && addressDTO.getUserAddress2() == null)) {
	        throw new CustomException(ErrorCode.NOT_FOUND_USER);
	    }
	    return addressDTO;
	}
	

	/**
	 * 개인정보수정
	 */
	@Override
	public void updateUserInfo(Long userNo, MemberInfoUpdateDTO dto) {
		getValidMember(userNo);
		
	    // 이메일 중복 확인 (본인 제외)
	    if (isEmailDuplicated(dto.getUserEmail(), userNo)) {
	        throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
	    }

	    // 연락처 중복 확인 (본인 제외)
	    if (isPhoneDuplicated(dto.getUserPhone(), userNo)) {
	        throw new CustomException(ErrorCode.DUPLICATE_PHONE);
	    }
		
		// 암호화 후 저장
		String encPhone = encryptionUtil.encrypt(dto.getUserPhone());
		String encEmail = encryptionUtil.encrypt(dto.getUserEmail());
		
		int result = memberInfoMapper.updateUserInfo(
				userNo, encPhone, encEmail, dto.getUserAddress1(), dto.getUserAddress2());
		
		if(result == 0) {
			throw new CustomException(ErrorCode.USER_UPDATE_FAILED);
		}
	}
	
	
	@Override
	public boolean isEmailDuplicated(String email, Long userNo) {
	    String encEmail = encryptionUtil.encrypt(email);
	    return memberInfoMapper.existsByEmail(encEmail, userNo) > 0;
	}

	@Override
	public boolean isPhoneDuplicated(String phone, Long userNo) {
	    String encPhone = encryptionUtil.encrypt(phone);
	    return memberInfoMapper.existsByPhone(encPhone, userNo) > 0;
	}
	

}
