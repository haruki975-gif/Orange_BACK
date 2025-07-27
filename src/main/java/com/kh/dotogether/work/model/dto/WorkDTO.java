package com.kh.dotogether.work.model.dto;

import lombok.Data;

@Data
public class WorkDTO {
	
	private Long requestUserNo;
	
	private String teamId;
	
	private String workId;

	private String title;

	private String content;

	private Long assigneeNo;

	private String assigneeName;
	
	private String assigneeProfile;

	private String endDate;

	private String status;

	private String prevStatus;

	private String type;
	
	private Long version;
	
}
