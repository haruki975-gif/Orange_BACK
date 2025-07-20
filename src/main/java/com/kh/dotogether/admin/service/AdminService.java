package com.kh.dotogether.admin.service;

import java.util.List;

import com.kh.dotogether.challenge.model.dto.ChallengeDTO;
import com.kh.dotogether.log.model.dto.LogDTO;
import com.kh.dotogether.member.model.dto.MemberDTO;

public interface AdminService {
    List<MemberDTO> findAllUsers();
    List<ChallengeDTO> findAllChallenges();
    List<LogDTO> findAllLogs();
}