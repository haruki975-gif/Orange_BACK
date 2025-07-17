package com.kh.dotogether.challenge.model.service;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.dotogether.auth.model.vo.CustomUserDetails;
import com.kh.dotogether.auth.service.AuthService;
import com.kh.dotogether.challenge.model.dao.ChallengeMapper;
import com.kh.dotogether.challenge.model.dto.ChallengeDTO;
import com.kh.dotogether.challenge.model.dto.ChallengePageDTO;
import com.kh.dotogether.challenge.model.vo.Challenge;
import com.kh.dotogether.file.service.FileService;
import com.kh.dotogether.profile.model.service.S3Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {
	
	private final ChallengeMapper challengeMapper;
	private final AuthService authService;
	private final FileService fileService;
	private final S3Service s3Service;

	@Override
	public void save(ChallengeDTO challenge, MultipartFile file) {
		CustomUserDetails user = authService.getUserDetails();
		
		Long memberNo = user.getUserNo();
		
		// challenge.setAuthor(String.valueOf(memberNo));
		Challenge requestData = null;
		
		if(file != null && !file.isEmpty()) {
			String filePath = s3Service.uploadFile(file);
			
			requestData =
				Challenge.builder()
						.challengeTitle(challenge.getChallengeTitle())
						.challengeContent(challenge.getChallengeContent())
						.challengeAuthor(String.valueOf(memberNo))
						.challengeComment(challenge.getChallengeComment())
						.challengeFileUrl(filePath)
						.build();
		} else {
			requestData = Challenge.builder()
					.challengeTitle(challenge.getChallengeTitle())
					.challengeContent(challenge.getChallengeContent())
					.challengeAuthor(String.valueOf(memberNo))
					.challengeComment(challenge.getChallengeComment())
					.build();
		}
		
		challengeMapper.save(requestData);
	}

	@Override
	public ChallengePageDTO findAll(int pageNo) {
		int size = 10;
		int offset = (pageNo - 1) * size;
		RowBounds rowBounds = new RowBounds(offset, size);
		List<ChallengeDTO> challenges = challengeMapper.findAll(rowBounds);
		long totalCount = challengeMapper.countAll();
		int totalPages = (int) Math.ceil((double) totalCount / size);
		for (int i = 0; i < challenges.size(); i++) {
	        challenges.get(i).setDisplayNo((int)(totalCount - offset - i));
	    }
		
		return new ChallengePageDTO(challenges, totalPages, totalCount);
	}

	@Override
	public ChallengeDTO findById(Long challengeNo) {
		// ChallengeDTO challenge = getChallengeOrThrow(challengeNo);
		return getChallengeOrThrow(challengeNo);
	}
	
	private ChallengeDTO getChallengeOrThrow(Long challengeNo) {
		ChallengeDTO challenge = challengeMapper.findById(challengeNo);
		if(challenge == null) {
			throw new RuntimeException("존재하지 않는 게시글 요청입니다.");
		}
		return challenge;
	}

	@Override
	public ChallengeDTO update(ChallengeDTO challenge, MultipartFile file) {
		if(file != null && !file.isEmpty()) {
			String filePath = fileService.store(file);
			challenge.setChallengeFileUrl(filePath);
		}
		challengeMapper.update(challenge);
		return challenge;
	}

	@Override
	public void markAsCompleted(Long challengeNo) {
	    challengeMapper.updateChallengeActive(challengeNo, "N"); // DB에 종료 상태 반영
	}

	@Override
	@Transactional
	public ChallengeDTO findAndIncrementViews(Long challengeNo) {
		challengeMapper.incrementViewCount(challengeNo);
        return challengeMapper.findById(challengeNo);
	}

}
