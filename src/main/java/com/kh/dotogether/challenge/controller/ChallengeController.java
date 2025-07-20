package com.kh.dotogether.challenge.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.dotogether.challenge.model.dto.ChallengeDTO;
import com.kh.dotogether.challenge.model.dto.ChallengePageDTO;
import com.kh.dotogether.challenge.model.service.ChallengeService;
import com.kh.dotogether.util.ResponseData;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge")
public class ChallengeController {
	
	private final ChallengeService challengeService;
	
	// 조회(select) : Get
	// 추가(insert) : Post
	// 수정(update) : Put
	// 삭제(delete) : Delete
		
	@PostMapping
	public ResponseEntity<?> save(@Valid ChallengeDTO challenge,
									@RequestParam(name="file", required=false) MultipartFile file){
		challengeService.save(challenge, file);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@GetMapping
	public ResponseEntity<ChallengePageDTO> findAll(@RequestParam(name="page",
														defaultValue="1")
														int page){
		return ResponseEntity.ok(challengeService.findAll(page));
	}
	
	@PutMapping("/{no}")
	public ResponseEntity<ChallengeDTO> update(@PathVariable(name="no") Long challengeNo,
												ChallengeDTO challenge,
												@RequestParam(name="file", required = false) MultipartFile file){
		challenge.setChallengeNo(challengeNo);
		return ResponseEntity.status(HttpStatus.CREATED)
							.body(challengeService.update(challenge, file));
	}
	
	@PostMapping("/{no}/complete")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> complete(@PathVariable(name="no") Long challengeNo) {
	    challengeService.markAsCompleted(challengeNo); // 상태 변경
	    return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{no}")
	public ResponseEntity<ChallengeDTO> getChallengeDetail(@PathVariable(name="no") @Min(value = 1, message = "작습니다") Long challengeNo) {
	    ChallengeDTO challenge = challengeService.findAndIncrementViews(challengeNo);
	    return ResponseEntity.ok(challenge);
	}
}