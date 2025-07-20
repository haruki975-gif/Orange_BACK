package com.kh.dotogether.section.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.dotogether.exception.exceptions.CustomException;
import com.kh.dotogether.global.enums.ErrorCode;
import com.kh.dotogether.schedule.model.dto.ScheduleDTO;
import com.kh.dotogether.section.model.dao.SectionMapper;
import com.kh.dotogether.section.model.dto.SectionDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {

	private final SectionMapper sectionMapper;

	@Override
	public SectionDTO setSection(SectionDTO sectionDTO) {
	    Long userNo = sectionDTO.getUserNo();

	    String title = sectionDTO.getSectionTitle();
	    if (title.isBlank()) throw new CustomException(ErrorCode.EMPTY_TITLE);
	    if (existsByTitle(title, userNo)) throw new CustomException(ErrorCode.DUPLICATE_SECTION_TITLE);

	    sectionDTO.setUserNo(userNo);
	    sectionDTO.setIsBaseSection(existsBaseSection(userNo) ? "N" : "Y");

	    sectionMapper.insertSection(sectionDTO);
	    
	    // 생성된 섹션 정보를 바로 반환
	    return sectionMapper.findSectionByNo(sectionDTO.getSectionNo(), userNo);
	}

	@Override
	public boolean existsByTitle(String sectionTitle, Long userNo) {
		return sectionMapper.existsByTitle(sectionTitle, userNo) > 0;
	}

	@Override
	public void updateSectionTitle(SectionDTO sectionDTO) {
	    Long userNo = sectionDTO.getUserNo();
		String title = sectionDTO.getSectionTitle();
		Long sectionNo = sectionDTO.getSectionNo();

		if (title.isBlank()) throw new CustomException(ErrorCode.EMPTY_TITLE);
		if (existsByTitle(title, userNo)) throw new CustomException(ErrorCode.DUPLICATE_SECTION_TITLE);

		sectionMapper.updateSectionTitle(title, userNo, sectionNo);
	}

	@Override
	public boolean existsBaseSection(Long userNo) {
		return sectionMapper.countBaseSection(userNo) > 0;
	}

	@Override
	public List<SectionDTO> findAllSectionsWithSchedules(Long userNo) {
		return sectionMapper.findAllSectionWithSchedule(userNo);
	}

	@Override
	public SectionDTO findSectionWithSchedules(Long sectionNo, Long userNo) {
		SectionDTO section = sectionMapper.findSectionWithSchedules(sectionNo, userNo);
		if (section == null) throw new CustomException(ErrorCode.SECTION_NOT_FOUND);
		return section;
	}

	@Override
	public void deleteSection(Long sectionNo, Long userNo) {
	    // 핵심 디버깅 로그
	    log.info("=== 섹션 삭제 디버깅 ===");
	    log.info("getCurrentUserNo() 결과: {}", userNo);
	    log.info("삭제할 섹션 번호: {}", sectionNo);
	    
	    SectionDTO sectionDTO = getSectionWithSchedules(sectionNo, userNo);
	    int sectionCount = sectionMapper.findBySection(userNo);
	    
	    log.info("DB에서 조회한 섹션 개수 (USER_NO={}): {}", userNo, sectionCount);
	    
	    // 추가 확인: 실제 81 유저의 섹션 개수도 확인
	    int actualCount = sectionMapper.findBySection(81L);
	    log.info("실제 81 유저 섹션 개수: {}", actualCount);
	    
	    if (sectionCount == 0) throw new CustomException(ErrorCode.SECTION_NOT_FOUND);
	    if (sectionCount == 1) throw new CustomException(ErrorCode.CANNOT_DELETE_LAST_SECTION);

		SectionDTO newBaseSection = findLastestSection(sectionNo, userNo); 
		log.info("다음으로 생성된 섹션 번호 : {}", newBaseSection);

		moveSchedulesToSection(sectionDTO, userNo, newBaseSection);

		if ("Y".equals(sectionDTO.getIsBaseSection())) {
			reassignBaseAndDelete(sectionNo, userNo, newBaseSection);
		} else {
			sectionMapper.deleteSection(sectionNo);
		}
	}


	private SectionDTO getSectionWithSchedules(Long sectionNo, Long userNo) {
		SectionDTO section = sectionMapper.findSectionByNo(sectionNo, userNo);
		List<ScheduleDTO> schedules = sectionMapper.findSchedulesBySectionNo(sectionNo);
		section.setSchedules(schedules);
		return section;
	}

	private void moveSchedulesToSection(SectionDTO sectionDTO, Long userNo, SectionDTO newBaseSection) {
		List<ScheduleDTO> schedules = sectionDTO.getSchedules();
		Long sectionNo = sectionDTO.getSectionNo();
		Long newSectionNo = newBaseSection.getSectionNo();

		if (schedules != null && !schedules.isEmpty()) {
			sectionMapper.moveSchedulesToSection(newSectionNo, sectionNo, userNo);
		}
	}

	private void reassignBaseAndDelete(Long sectionNo, Long userNo, SectionDTO newBaseSection) {
		newBaseSection.setIsBaseSection("Y");
		newBaseSection.setUserNo(userNo);
		sectionMapper.updateBaseSection(newBaseSection);
		log.info("기준 섹션으로 승격 : {}", newBaseSection);
		sectionMapper.deleteSection(sectionNo);
	}

	private SectionDTO findLastestSection(Long sectionNo, Long userNo) {
		return sectionMapper.findLastestSection(sectionNo, userNo);
	}


	@Override
	public SectionDTO findSectionByNo(Long sectionNo, Long userNo) {
	    SectionDTO section = sectionMapper.findSectionByNo(sectionNo, userNo);
	    if (section == null) {
	        throw new CustomException(ErrorCode.SECTION_NOT_FOUND);
	    }
	    return section;
	}

}

