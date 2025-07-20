package com.kh.dotogether.schedule.model.service;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kh.dotogether.auth.model.vo.CustomUserDetails;
import com.kh.dotogether.exception.exceptions.CustomException;
import com.kh.dotogether.global.enums.ErrorCode;
import com.kh.dotogether.schedule.model.dao.ScheduleMapper;
import com.kh.dotogether.schedule.model.dto.MoveScheduleRequest;
import com.kh.dotogether.schedule.model.dto.ScheduleDTO;
import com.kh.dotogether.section.model.dao.SectionMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService{
	
	private final ScheduleMapper scheduleMapper;
	private final SectionMapper sectionMapper;

	@Override
	public void setSchedule(ScheduleDTO scheduleDTO) {
		String title = scheduleDTO.getScheduleTitle();
		Long userNo = getCurrentUserNo();
		
		scheduleDTO.setScheduleTitle(title);
		scheduleDTO.setSectionNo(scheduleDTO.getSectionNo());
		scheduleDTO.setUserNo(userNo);
		scheduleMapper.insertSchedule(scheduleDTO);
	}
	
	
	@Override
	public void setScheduleInBaseSection(ScheduleDTO scheduleDTO) {
	    Long userNo = scheduleDTO.getUserNo();  // 컨트롤러에서 세팅한 userNo 사용

	    // 기준 섹션 조회
	    Long baseSectionNo = sectionMapper.selectBaseSectionNoByUserNo(userNo);
	    if (baseSectionNo == null) {
	        throw new CustomException(ErrorCode.BASE_SECTION_NOT_FOUND);
	    }

	    scheduleDTO.setSectionNo(baseSectionNo);
	    scheduleMapper.insertSchedule(scheduleDTO);
	}



	

	@Override
	public ScheduleDTO findScheduleById(Long scheduleNo) {
		Long userNo = getCurrentUserNo();
		ScheduleDTO schedule = scheduleMapper.findScheduleById(scheduleNo, userNo);
		
		if(schedule == null) {
			throw new CustomException(ErrorCode.SCHEDULE_NOT_FOUND);
		}
		return schedule;
	}


	@Override
	public void updateSchedule(ScheduleDTO scheduleDTO) {
		String scheduleTitle = scheduleDTO.getScheduleTitle();
		
		if(scheduleTitle.isBlank()) {
			throw new CustomException(ErrorCode.EMPTY_TITLE);
		}
		scheduleMapper.updateSchedule(scheduleDTO);
	}
	
	@Override
	public void deleteSchedule(Long scheduleNo, Long userNo) {
		scheduleMapper.deleteSchedule(scheduleNo, userNo);
	}

	
	@Override
	public void updateScheduleSection(MoveScheduleRequest request) {
		Long userNo = getCurrentUserNo();
		Long scheduleNo = request.getScheduleNo();
		Long targetSectionNo = request.getTargetSectionNo();
		scheduleMapper.updateScheduleSection(targetSectionNo, scheduleNo, userNo);
		
	}
	
	
	private Long getCurrentUserNo() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof CustomUserDetails userDetails) {
			return userDetails.getUserNo();
		}
		throw new CustomException(ErrorCode.NOT_FOUND_USER);
	}



	
	
}
