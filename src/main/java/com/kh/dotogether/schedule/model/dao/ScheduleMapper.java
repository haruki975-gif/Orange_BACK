package com.kh.dotogether.schedule.model.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.dotogether.schedule.model.dto.MoveScheduleRequest;
import com.kh.dotogether.schedule.model.dto.ScheduleDTO;

@Mapper
public interface ScheduleMapper {

	void insertSchedule(ScheduleDTO scheduleDTO);

	ScheduleDTO findScheduleById(@Param("scheduleNo") Long scheduleNo, @Param("userNo") Long userNo);

	void updateSchedule(ScheduleDTO scheduleDTO);

	void deleteSchedule(@Param("scheduleNo") Long scheduleNo, @Param("userNo") Long userNo);

	void updateScheduleSection(@Param("targetSectionNo") Long targetSectionNo,
							   @Param("scheduleNo") Long scheduleNo,
							   @Param("userNo") Long userNo);

	Long selectBaseSectionNoByUserNo(Long userNo);


}
