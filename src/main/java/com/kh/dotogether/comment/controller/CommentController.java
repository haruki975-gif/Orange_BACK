package com.kh.dotogether.comment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.dotogether.auth.model.vo.CustomUserDetails;
import com.kh.dotogether.auth.service.AuthService;
import com.kh.dotogether.comment.model.dao.CommentMapper;
import com.kh.dotogether.comment.model.dto.CommentDTO;
import com.kh.dotogether.comment.model.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge/comment")
public class CommentController {
	
	private final CommentService commentService;
	private final AuthService authService;
    private final CommentMapper commentMapper;

	
	@PostMapping
	public ResponseEntity<?> insertComment(@Valid @ModelAttribute CommentDTO comment,
											@RequestParam(name = "file", required = false) MultipartFile file) {
		commentService.insertComment(comment, file);
		return ResponseEntity.status(HttpStatus.CREATED).body(null);
		
	}
	
	@GetMapping
	public ResponseEntity<List<CommentDTO>> selectCommentList(@RequestParam("postId") Long postId) {
	    return ResponseEntity.ok(commentService.selectCommentList(postId));
	}
	
	@PutMapping("/{commentNo}")
	public ResponseEntity<?> updateComment(@PathVariable(name = "commentNo") Long commentNo,
											@Valid @ModelAttribute CommentDTO comment,
											@RequestParam(name = "file", required = false) MultipartFile file,
											@RequestParam(name = "commentFileUrl", required = false) String commentFileUrl,
											@RequestParam(name = "removeImage", required = false) String removeImage) {
	    CustomUserDetails userDetails = (CustomUserDetails) authService.getUserDetails();
	    if (userDetails == null) {
	        log.warn("인증된 사용자 정보가 없습니다.");
	    }

	    Long userNo = userDetails.getUserNo();
	    log.info("JWT에서 추출한 userNo: {}", userNo);

	    Long commentWriterNo = commentMapper.selectCommentWriterNo(commentNo);
	    log.info("DB에서 조회한 commentWriterNo: {}", commentWriterNo);
	    log.info("요청한 commentNo: {}", commentNo);
	    
	    if (commentWriterNo == null) {
	        log.warn("해당 댓글 번호가 존재하지 않음: {}", commentNo);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글을 찾을 수 없습니다.");
	    }

	    if (!userNo.equals(commentWriterNo)) {
	        log.warn("권한 없음: 작성자와 로그인 사용자 불일치");
	        throw new AccessDeniedException("댓글 작성자만 수정 가능");
	    }
	    
	    try {
	        comment.setCommentNo(commentNo);
	        
	        if ((file == null || file.isEmpty()) && commentFileUrl != null) {
	            comment.setCommentFileUrl(commentFileUrl);
	        }
	        
	        if ("true".equals(removeImage)) {
	            comment.setCommentFileUrl(null);
	            file = null;
	        } else if ((file == null || file.isEmpty()) && commentFileUrl != null) {
	            comment.setCommentFileUrl(commentFileUrl);
	        }
	        
	        commentService.updateComment(comment, file);
	        return ResponseEntity.ok().body("댓글이 수정되었습니다.");
	    } catch (Exception e) {
	        log.error("댓글 수정 중 오류", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정 실패");
	    }
	}

	
	@DeleteMapping("/{commentNo}")
	public ResponseEntity<?> softDeleteComment(@PathVariable(name = "commentNo") Long commentNo) {
		CustomUserDetails userDetails = (CustomUserDetails) authService.getUserDetails();
	    Long userNo = userDetails.getUserNo();

	    Long commentWriterNo = commentMapper.selectCommentWriterNo(commentNo);

	    if (!userNo.equals(commentWriterNo)) {
	        throw new AccessDeniedException("댓글 작성자만 삭제 가능");
	    }
	    
	    try {
	        commentService.softDeleteComment(commentNo);
	        return ResponseEntity.ok().body("댓글이 삭제되었습니다.");
	    } catch (Exception e) {
	        log.error("댓글 삭제 중 오류", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 실패");
	    }
	}
	
}