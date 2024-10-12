package lems.cowshed.service;

import lems.cowshed.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;

    //회원 정보 만료 체크
    //계정의 서비스 기간이 만료, 유효 기간이 끝난 경우
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //회원 정보 lock 체크
    //비밀 번호 여러번 잘못 입력과 같은 상황
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 사용자 자격 증명 체크
    // 비밀 번호 일정 기간 변경 정책 -> 만료된 비밀 번호
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정 활성화
    // 이메일 인증을 통해 계정을 활성화 해야 하는 경우
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public String getUserEmail() {return user.getEmail();}

}
