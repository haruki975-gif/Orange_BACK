package com.kh.dotogether.comment.model.vo;

import java.sql.Date;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Comment {
	
	private Long commentNo;
	private String commentContent;
	private Long commentWriter;
	private LocalDateTime createDate;
	private Long refBoardNo;
	private String commentFileUrl;
	private String isDeleted;
	
}