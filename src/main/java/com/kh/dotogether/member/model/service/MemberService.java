package com.kh.dotogether.member.model.service;

import java.util.List;

import com.kh.dotogether.member.model.dto.MemberDTO;
import com.kh.dotogether.member.model.dto.MemberIdResponseDTO;

public interface MemberService {

    /**
     * 회원가입
     * @param memberDTO
     */
    void signUp(MemberDTO memberDTO);

    /**
     * 아이디(userId) 중복 확인
     * @param userId
     * @return true → 중복, false → 사용 가능
     */
    boolean isUserIdDuplicated(String userId);

    /**
     * 이메일 중복 확인
     * @param email
     * @return true → 중복, false → 사용 가능
     */
    boolean isEmailDuplicated(String email);

    /**
     * 연락처 중복 확인
     * @param phone
     * @return true → 중복, false → 사용 가능
     */
    boolean isPhoneDuplicated(String phone);

    /**
     * 회원 탈퇴
     * @param userNo
     * @return true → 성공, false → 실패
     */
    boolean deleteUser(Long userNo, String authorizationHeader);

    /**
     * 회원 정보 조회 (userNo 기준)
     * @param userNo
     * @return MemberDTO
     */
    MemberDTO findByUserNo(Long userNo);
    
    /**
     * 비밀번호 찾기 1단계 - 아이디 조회
     * @param userId
     * @return
     */
    MemberDTO findByUserId(String userId);

    /**
     * 비밀번호 재설정
     * @param userId
     * @param email
     * @param newPassword
     */
    void resetPassword(String userId, String email, String newPassword);
    
    /**
     * 아이디 찾기(이름, 이메일)
     * @param userId
     * @param userEmail
     * @return
     */
    MemberIdResponseDTO findUserId(String userName, String userEmail);
    
    List<MemberDTO> findAll(int page, int size);
    int countAll();
    
    /**
     * 회원 상태 업데이트 (활동중 'Y' / 정지 'N')
     * @param userId
     * @param newStatus
     * @return 업데이트 성공 여부
     */
    boolean updateUserStatus(String userId, String newStatus);

}
