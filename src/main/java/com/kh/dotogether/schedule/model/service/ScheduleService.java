package com.kh.dotogether.schedule.model.service;

import com.kh.dotogether.schedule.model.dto.MoveScheduleRequest;
import com.kh.dotogether.schedule.model.dto.ScheduleDTO;

import jakarta.validation.Valid;




public interface ScheduleService {

	void setSchedule(ScheduleDTO scheduleDTO);

	ScheduleDTO findScheduleById(Long scheduleNo);

	void updateSchedule(ScheduleDTO scheduleDTO);

	void deleteSchedule(Long scheduleNo, Long userNo);

	void updateScheduleSection(MoveScheduleRequest request);

	void setScheduleInBaseSection(@Valid ScheduleDTO scheduleDTO);


}
