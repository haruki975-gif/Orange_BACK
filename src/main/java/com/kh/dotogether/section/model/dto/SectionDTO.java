package com.kh.dotogether.section.model.dto;

import java.util.List;

import com.kh.dotogether.schedule.model.dto.ScheduleDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class SectionDTO {
	
	private Long sectionNo;
	private Long userNo;
	
	@Size(max = 30, message = "30자 이내로 입력해주세요.")
	@NotBlank(message = "빈 값은 입력 불가능")
	private String sectionTitle;
	private String isBaseSection;
	private String createdAt;
	private List<ScheduleDTO> schedules;
}
