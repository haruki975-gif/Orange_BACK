package com.kh.dotogether.section.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.dotogether.auth.model.vo.CustomUserDetails;
import com.kh.dotogether.section.model.dto.SectionDTO;
import com.kh.dotogether.section.model.service.SectionService;
import com.kh.dotogether.util.ResponseData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/section")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    // 섹션 추가
    @PostMapping
    public ResponseEntity<ResponseData> addSection(
            @AuthenticationPrincipal CustomUserDetails userDetails, // 인증된 사용자 정보
            @RequestBody @Valid SectionDTO sectionDTO) {

        Long userNo = userDetails.getUserNo();
        sectionDTO.setUserNo(userNo); // 서버가 userNo 강제 주입

        SectionDTO newSection = sectionService.setSection(sectionDTO);

        return ResponseEntity.ok(ResponseData.builder()
                .code("200")
                .message("섹션 추가에 성공했습니다.")
                .items(List.of(newSection))
                .build());
    }

    @GetMapping("/check-title")
    public ResponseEntity<ResponseData> checkSectionTitle(
            @RequestParam("sectionTitle") String sectionTitle,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userNo = userDetails.getUserNo();

        boolean exists = sectionService.existsByTitle(sectionTitle, userNo);
        String message = exists ? "중복된 제목이 존재합니다." : "사용 가능한 제목입니다.";
        String code = exists ? "409" : "200";

        return ResponseEntity.ok(ResponseData.builder()
                .code(code)
                .message(message)
                .items(Collections.emptyList())
                .build());
    }

    // 내 섹션 및 일정 전체 조회
    @GetMapping("/dashboard")
    public ResponseEntity<ResponseData> findAllSectionsWithSchedules(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userNo = userDetails.getUserNo();
        List<SectionDTO> sections = sectionService.findAllSectionsWithSchedules(userNo);

        return ResponseEntity.ok(ResponseData.builder()
                .code("200")
                .message("섹션 및 일정 전체 조회 성공")
                .items(sections)
                .build());
    }

    // 특정 섹션 조회 (내 소유 섹션인지 꼭 체크 필요)
    @GetMapping("/{sectionNo}")
    public ResponseEntity<ResponseData> getSectionWithSchedules(
            @PathVariable("sectionNo") Long sectionNo,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userNo = userDetails.getUserNo();
        SectionDTO section = sectionService.findSectionWithSchedules(sectionNo, userNo);

        return ResponseEntity.ok(ResponseData.builder()
                .code("200")
                .message("섹션 및 스케줄 조회 완료")
                .items(List.of(section))
                .build());
    }

    // 섹션 제목 수정
    @PostMapping("/title-update")
    public ResponseEntity<ResponseData> updateSectionTitle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid SectionDTO sectionDTO) {

        Long userNo = userDetails.getUserNo();
        sectionDTO.setUserNo(userNo);

        sectionService.updateSectionTitle(sectionDTO);

        return ResponseEntity.ok(ResponseData.builder()
                .code("200")
                .message("제목 수정 완료")
                .items(Collections.emptyList())
                .build());
    }

    // 섹션 삭제
    @DeleteMapping("/{sectionNo}")
    public ResponseEntity<ResponseData> deleteSection(
            @PathVariable("sectionNo") Long sectionNo,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userNo = userDetails.getUserNo();
        sectionService.deleteSection(sectionNo, userNo);

        return ResponseEntity.ok(ResponseData.builder()
                .code("200")
                .message("섹션이 성공적으로 삭제되었습니다.")
                .items(Collections.emptyList())
                .build());
    }
}
