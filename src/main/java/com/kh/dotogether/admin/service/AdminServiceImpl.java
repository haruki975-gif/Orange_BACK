package com.kh.dotogether.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.dotogether.challenge.model.dao.ChallengeMapper;
import com.kh.dotogether.challenge.model.dto.ChallengeDTO;
import com.kh.dotogether.log.model.dao.LogMapper;
import com.kh.dotogether.log.model.dto.LogDTO;
import com.kh.dotogether.member.model.dao.MemberMapper;
import com.kh.dotogether.member.model.dto.MemberDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final MemberMapper memberMapper;
    private final ChallengeMapper challengeMapper;
    private final LogMapper logMapper;

    @Override
    public List<MemberDTO> findAllUsers() {
    	
        return null;
    }

    @Override
    public List<ChallengeDTO> findAllChallenges() {
    	
        return null;
    }

    @Override
    public List<LogDTO> findAllLogs() {
    	
        return null;
    }
}