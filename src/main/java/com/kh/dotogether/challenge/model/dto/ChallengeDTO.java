package com.kh.dotogether.challenge.model.dto;


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public class ChallengeDTO {
	
	private Long challengeNo;
    private String challengeTitle;
    private String challengeContent;
    private String challengeAuthor;
    private String challengeComment;
    private String challengeFileUrl;
    private String status;
    private int challengeViews;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private Date challengeDate;
    private Integer displayNo;
    
}