package com.kh.dotogether.comment.model.dto;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
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
public class CommentDTO {
	
	private Long commentNo;
	@NotBlank(message="댓글을 작성해주세요")
	private String commentContent;
	private Long commentWriterNo;
	private String commentWriter;
	private LocalDateTime createDate;
	private Long refBoardNo;
	private String commentFileUrl;
	private String isDeleted;
	
}
