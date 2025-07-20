package com.kh.dotogether.comment.model.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kh.dotogether.auth.model.vo.CustomUserDetails;
import com.kh.dotogether.auth.service.AuthService;
import com.kh.dotogether.challenge.model.service.ChallengeService;
import com.kh.dotogether.comment.model.dao.CommentMapper;
import com.kh.dotogether.comment.model.dto.CommentDTO;
import com.kh.dotogether.comment.model.vo.Comment;
import com.kh.dotogether.exception.exceptions.InvalidUserRequestException;
import com.kh.dotogether.file.service.FileService;
import com.kh.dotogether.profile.model.service.S3Service;
import com.kh.dotogether.auth.model.vo.CustomUserDetails;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
	
	private final CommentMapper commentMapper;
	private final ChallengeService challengeService;
	private final AuthService authService;
	private final FileService fileService;
	private final S3Service s3Service;

	@Override
	public void insertComment(CommentDTO comment, MultipartFile file) {
		String tokenUserNo =
			String.valueOf(((CustomUserDetails)authService.getUserDetails()).getUserNo());
		
		if(tokenUserNo == null) {
			throw new InvalidUserRequestException("이름을 알 수 없습니다");
		}
		
		String filePath = null;

		if(file != null) {
			filePath = s3Service.uploadFile(file);
		}
		
		
		Comment requestData =
			Comment.builder()
					.commentWriter(Long.parseLong(tokenUserNo))
					.commentContent(comment.getCommentContent())
					.refBoardNo(comment.getRefBoardNo())
					.commentFileUrl(filePath)
					.build();
		
		commentMapper.insertComment(requestData);
	}

	@Override
	public List<CommentDTO> selectCommentList(Long boardNo) {
		return commentMapper.selectCommentList(boardNo);
	}

	@Override
	public CommentDTO update(CommentDTO comment, MultipartFile file) {
		if(file != null && !file.isEmpty()) {
			String filePath = s3Service.uploadFile(file);
			comment.setCommentFileUrl(filePath);
		}
		
		commentMapper.update(comment);
		return comment;
	}

	// 댓글 수정
	@Override
	public void updateComment(CommentDTO comment, MultipartFile file) {
	    // 현재 로그인 사용자 번호
	    Long loginUserNo = ((CustomUserDetails) authService.getUserDetails()).getUserNo();

	    // DB에서 기존 댓글 작성자 조회 (CommentMapper에 메서드 필요)
	    Long commentWriterNo = commentMapper.selectCommentWriterNo(comment.getCommentNo());
	    
	    if (!loginUserNo.equals(commentWriterNo)) {
	        throw new InvalidUserRequestException("댓글 수정 권한이 없습니다.");
	    }
	    
	    // 새 파일이 있을 때 업로드
	    if (file != null && !file.isEmpty()) {
	        // 기존 이미지가 있으면 삭제 (옵션)
	        if (comment.getCommentFileUrl() != null) {
	            s3Service.deleteFile(comment.getCommentFileUrl());
	        }
	        String filePath = s3Service.uploadFile(file);
	        comment.setCommentFileUrl(filePath);
	    } 
	    // 새 파일 없고, commentFileUrl이 null이라면 기존 이미지 삭제 요청으로 간주
	    else if (comment.getCommentFileUrl() == null) {
	        // 기존 이미지 삭제
	        // DB에 저장된 기존 이미지 URL 조회
	        String existingFileUrl = commentMapper.selectCommentFileUrl(comment.getCommentNo());
	        if (existingFileUrl != null) {
	            s3Service.deleteFile(existingFileUrl);
	        }
	        comment.setCommentFileUrl(null);
	    }

	    if (file != null && !file.isEmpty()) {
	        String filePath = s3Service.uploadFile(file);
	        comment.setCommentFileUrl(filePath);
	    }
	    
	    commentMapper.updateComment(comment);
	}

	@Override
	public void softDeleteComment(Long commentNo) {
	    Long loginUserNo = ((CustomUserDetails) authService.getUserDetails()).getUserNo();
	    Long commentWriterNo = commentMapper.selectCommentWriterNo(commentNo);

	    if (!loginUserNo.equals(commentWriterNo)) {
	        throw new InvalidUserRequestException("댓글 삭제 권한이 없습니다.");
	    }

	    commentMapper.softDeleteComment(commentNo);
	}


}
