package com.kh.dotogether.challenge.model.vo;


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Challenge {
	
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
    
}