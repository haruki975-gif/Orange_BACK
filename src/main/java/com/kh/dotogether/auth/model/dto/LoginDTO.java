package com.kh.dotogether.auth.model.dto;

import org.springframework.data.annotation.Id;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginDTO {
	
	// 비밀번호 보완

	@NotBlank(message = "아이디는 반드시 입력해야 합니다.")
	@Pattern(
	        regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z0-9]{6,15}$",
	        message = "아이디는 6~15자의 영문/숫자 조합만 가능합니다."
	    )
	private String userId;
	
	@NotBlank(message = "비밀번호는 반드시 입력해야 합니다.")
	private String userPw;
}
