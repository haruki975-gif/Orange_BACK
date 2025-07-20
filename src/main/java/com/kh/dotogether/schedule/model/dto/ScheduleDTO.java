package com.kh.dotogether.schedule.model.dto;

import java.sql.Date;

import org.springframework.format.annotation.DateTimeFormat;

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
public class ScheduleDTO {

    private Long scheduleNo;
    private Long userNo;  
    private Long sectionNo;

    @Size(max = 50, message = "제목은 50자 이내로 입력해주세요.")
    @NotBlank(message = "제목은 필수입니다.")
    private String scheduleTitle;

    @Size(max = 300, message = "내용은 300자 이내로 입력해주세요.")
    private String scheduleContent;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate; // 선택적 시작일

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dueDate; // 선택적 마감일

    private char isCompleted; // 'Y' or 'N'

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createdAt;
}
