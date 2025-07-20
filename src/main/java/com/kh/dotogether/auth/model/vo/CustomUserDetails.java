package com.kh.dotogether.auth.model.vo;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class CustomUserDetails implements UserDetails {
	
	private final Long userNo;
	private final String userId;
	private final String password;
	private final String userName;
	private final String role;
	private Collection<? extends GrantedAuthority> authorities;
	
	// 사용자 이름
	public String getUserName() {
	    return this.userName;
	}
	
	@Override
    public String getUsername() {
        return userId;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
