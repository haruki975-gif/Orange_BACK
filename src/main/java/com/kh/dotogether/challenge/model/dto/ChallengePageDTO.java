package com.kh.dotogether.challenge.model.dto;

import java.util.List;

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
public class ChallengePageDTO {
	
	private List<ChallengeDTO> challenges;
	private int totalPages;
	private long totalCount;
	
}