package com.kh.dotogether.admin.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.dotogether.admin.service.AdminService;
import com.kh.dotogether.challenge.model.dto.ChallengeDTO;
import com.kh.dotogether.log.model.dto.LogDTO;
import com.kh.dotogether.log.model.service.LogService;
import com.kh.dotogether.member.model.dto.MemberDTO;
import com.kh.dotogether.member.model.service.MemberService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	private final MemberService memberService;
    private final AdminService adminService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;
    
    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> body, Principal principal) {
        String inputPassword = body.get("password");

        UserDetails user = userDetailsService.loadUserByUsername(principal.getName());

        if (!passwordEncoder.matches(inputPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호 불일치");
        }

        return ResponseEntity.ok("인증 성공");
    }

    // 회원 목록 조회
    @GetMapping("/members")
    public ResponseEntity<Map<String, Object>> getAllMembers(
            @RequestParam(defaultValue = "1", name = "page") int page,
            @RequestParam(defaultValue = "5", name = "size") int size) {

        List<MemberDTO> members = memberService.findAll(page, size);
        int total = memberService.countAll();

        Map<String, Object> result = new HashMap();
        result.put("members", members);
        result.put("total", total);

        return ResponseEntity.ok(result);
    }
    
    // 회원 상태 변경
    @PutMapping("/members/{userId}/status")
    public ResponseEntity<?> changeUserStatus(
            @PathVariable(name="userId") String userId,
            @RequestBody Map<String, String> body) {
        String newStatus = body.get("userStatus");
        memberService.updateUserStatus(userId, newStatus);
        String userName = memberService.findByUserId(userId).getUserName();
    	logService.insertLog(userId, userName, "회원상태 변경");
        return ResponseEntity.ok().build();
    }

    // 챌린지방 목록 조회
    @GetMapping("/challenges")
    public ResponseEntity<List<ChallengeDTO>> getAllChallenges() {
        List<ChallengeDTO> challenges = adminService.findAllChallenges();
        return ResponseEntity.ok(challenges);
    }

    // 로그 기록 조회
    @GetMapping("/logs")
    public ResponseEntity<List<LogDTO>> getAllLogs() {
        List<LogDTO> logs = adminService.findAllLogs();
        return ResponseEntity.ok(logs);
    }
    
}