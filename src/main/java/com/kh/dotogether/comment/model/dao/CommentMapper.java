package com.kh.dotogether.comment.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import com.kh.dotogether.comment.model.dto.CommentDTO;
import com.kh.dotogether.comment.model.vo.Comment;

@Mapper
public interface CommentMapper {
	
	void insertComment(Comment comment);
	
	List<CommentDTO> selectCommentList(Long boardNo);
	
	@Update("UPDATE TB_CHALLENGE_COMMENT SET COMMENT_CONTENT = #{commentContent}, COMMENT_FILE_URL = #{commentFileUrl} WHERE COMMENT_NO = #{commentNo}")
	void update(CommentDTO comment);
	
	Long selectCommentWriterNo(@Param("commentNo") Long commentNo);

	void updateComment(CommentDTO comment);
	
	String selectCommentFileUrl(Long commentNo);
	
	void softDeleteComment(Long commentNo);
	
}