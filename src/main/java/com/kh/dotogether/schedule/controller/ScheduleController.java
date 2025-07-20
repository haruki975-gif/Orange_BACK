package com.kh.dotogether.schedule.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.kh.dotogether.auth.model.vo.CustomUserDetails;
import com.kh.dotogether.schedule.model.dto.MoveScheduleRequest;
import com.kh.dotogether.schedule.model.dto.ScheduleDTO;
import com.kh.dotogether.schedule.model.service.ScheduleService;
import com.kh.dotogether.util.ResponseData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/schedule")
@RequiredArgsConstructor
public class ScheduleController {
	
	private final ScheduleService scheduleService;

	// ✅ 일정 추가
	@PostMapping
	public ResponseEntity<ResponseData> setSchedule(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestBody @Valid ScheduleDTO scheduleDTO) {

		scheduleDTO.setUserNo(userDetails.getUserNo()); // 인증 사용자로 덮어쓰기
		scheduleService.setSchedule(scheduleDTO);

		return ResponseEntity.ok(ResponseData.builder()
				.code("200")
				.message("일정 추가에 성공했습니다.")
				.items(List.of(scheduleDTO))
				.build());
	}
	
	
	@PostMapping("/base-section-set")
	public ResponseEntity<ResponseData> addToBaseSection(
	    @AuthenticationPrincipal CustomUserDetails userDetails,
	    @RequestBody @Valid ScheduleDTO scheduleDTO) {
	    scheduleDTO.setUserNo(userDetails.getUserNo());
	    scheduleService.setScheduleInBaseSection(scheduleDTO);

	    return ResponseEntity.ok(ResponseData.builder()
	        .code("200")
	        .message("기준 섹션에 일정 추가 성공")
	        .items(List.of(scheduleDTO))
	        .build());
	}


	// ✅ 일정 조회
	@GetMapping("/{scheduleNo}")
	public ResponseEntity<ResponseData> findSchedule(@PathVariable("scheduleNo") Long scheduleNo) {
		ScheduleDTO schedule = scheduleService.findScheduleById(scheduleNo);
		return ResponseEntity.ok(ResponseData.builder()
				.code("200")
				.message("일정 상세 조회 성공")
				.items(Collections.singletonList(schedule))
				.build());
	}

	// ✅ 일정 수정
	@PostMapping("/update")
	public ResponseEntity<ResponseData> updateSchedule(
			@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestBody @Valid ScheduleDTO scheduleDTO) {

		scheduleDTO.setUserNo(userDetails.getUserNo()); // 인증 사용자 기준으로 설정
		scheduleService.updateSchedule(scheduleDTO);

		return ResponseEntity.ok(ResponseData.builder()
				.code("200")
				.message("일정 수정 완료")
				.items(Collections.emptyList())
				.build());
	}

	// ✅ 일정 삭제
	@DeleteMapping("/{scheduleNo}")
	public ResponseEntity<ResponseData> deleteSection(
			@PathVariable("scheduleNo") Long scheduleNo,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userNo = userDetails.getUserNo();
		scheduleService.deleteSchedule(scheduleNo, userNo); // 서비스에서 소유자 확인도 함께 수행
		return ResponseEntity.ok(ResponseData.builder()
				.code("200")
				.message("일정이 성공적으로 삭제되었습니다.")
				.items(Collections.emptyList())
				.build());
	}

	// ✅ 일정 섹션 이동
	@PostMapping("/update-schedule-section")
	public ResponseEntity<ResponseData> updateScheduleSection(
			@RequestBody MoveScheduleRequest request) {

		scheduleService.updateScheduleSection(request);

		return ResponseEntity.ok(ResponseData.builder()
				.code("200")
				.message("일정 이동 완료")
				.items(Collections.emptyList())
				.build());
	}
}
