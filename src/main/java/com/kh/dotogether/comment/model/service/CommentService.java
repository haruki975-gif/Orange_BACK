package com.kh.dotogether.comment.model.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kh.dotogether.comment.model.dto.CommentDTO;

public interface CommentService {
	void insertComment(CommentDTO comment, MultipartFile file);
	List<CommentDTO> selectCommentList(Long boardNo);
	CommentDTO update(CommentDTO comment, MultipartFile file);
	void updateComment(CommentDTO comment, MultipartFile file);
	void softDeleteComment(Long commentNo);
}
