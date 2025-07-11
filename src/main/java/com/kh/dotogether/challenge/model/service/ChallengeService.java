package com.kh.dotogether.challenge.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kh.dotogether.challenge.model.dto.ChallengeDTO;
import com.kh.dotogether.challenge.model.dto.ChallengePageDTO;

public interface ChallengeService {
	
	void save(ChallengeDTO challenge, MultipartFile file);
	
	ChallengeDTO findAndIncrementViews(Long challengeNo);
	
	ChallengeDTO findById(Long challengeNo);
	
	ChallengeDTO update(ChallengeDTO challenge, MultipartFile file);
	
	void markAsCompleted(Long challengeNo);
	
	ChallengePageDTO findAll(int pageNo);
	
}
