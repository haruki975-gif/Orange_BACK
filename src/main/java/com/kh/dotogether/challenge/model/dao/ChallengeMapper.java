package com.kh.dotogether.challenge.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.session.RowBounds;

import com.kh.dotogether.challenge.model.dto.ChallengeDTO;
import com.kh.dotogether.challenge.model.vo.Challenge;

@Mapper
public interface ChallengeMapper {
	
	Long countAll();
	
	void save(Challenge challenge);
	
	List<ChallengeDTO> findAll(RowBounds rb);
	
	void incrementViewCount(Long challengeNo);
	
	ChallengeDTO findById(Long challengeNo);
	
	@Update("UPDATE TB_CHALLENGE SET CHALLENGE_TITLE = #{challengeTitle}, CHALLENGE_CONTENT = #{challengeContent}, CHALLENGE_FILE_URL = #{challengeFileUrl} WHERE CHALLENGE_NO = #{challengeNo}")
	void update(ChallengeDTO challenge);
	
	int updateChallengeActive(@Param("challengeNo") Long challengeNo, @Param("status") String status);
	
}