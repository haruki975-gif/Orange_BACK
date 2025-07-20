package com.kh.dotogether.log.model.vo;

import java.sql.Date;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Log {
	private Long logNo;
	private String logUserId;
	private String logUserName;
    private String logValue;
    private Date logDate;
}