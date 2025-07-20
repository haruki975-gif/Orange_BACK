package com.kh.dotogether.schedule.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MoveScheduleRequest {
	
	private Long scheduleNo;
	private Long targetSectionNo;
}
