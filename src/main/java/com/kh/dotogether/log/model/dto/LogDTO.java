package com.kh.dotogether.log.model.dto;


import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LogDTO {
	private Long logNo;
	private String logUserId;
	private String logUserName;
    private String logValue;
    private String logDate;
}