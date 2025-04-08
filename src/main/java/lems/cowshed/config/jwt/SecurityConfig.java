package lems.cowshed.config.jwt;

import lems.cowshed.config.jwt.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    //인증 설정 클래스
    private final AuthenticationConfiguration authenticationConfiguration;
    //jwt 생성 클래스
    private final JwtUtil jwtUtil;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }


    // 암호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // FilterChainProxy 등록 되는 chain 설정
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        /*
        Cross-Site request Forgery 공격 방어용 토큰
        악의적 사이트 접속 시 사용자의 쿠키를 이용해 요청을 보내는 방법
        쿠키 브라우저 모든 도메인 자동 요청에 포함 -> csrf 공격에 취약
        jwt 다른 도메인 요청시 자동 jwt 포함x -> csrf 공격 위험 줄어듬
        JWT 방식은 사용x
         */
        http
                .csrf((auth) -> auth.disable());

        /*
        폼 로그인 방식 사용x
        인증 안된 사용자 -> /login 으로 redirect 하는 기능
        JWT 구현 시 필요X
         */
        http
                .formLogin((auth) -> auth.disable());

        /*
        http basic 인증 방식 사용x
        헤더에 id 비밀 번호를 적어 인증 하는 방법
        JWT 사용시 필요X
         */
        http
                .httpBasic((auth) -> auth.disable());

        // http 인증 url
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/users/signUp", "/users/login", "/users/validate/**", "/mails/**").permitAll()
                        .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated());

        /*
         로그인 필터 등록
         사용자 이름, 비밀 번호 기반 폼 로그인 필터
         Post 방식의 /login 요청을 받아 username, password 파라미터 처리
         AuthenticationManager 통해 자격 증명 인증 -> 성공 시 SecurityContextHolder 인증 객체를 저장 하여 세션 생성
         */
        UsernamePasswordAuthenticationFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil);
        loginFilter.setFilterProcessesUrl("/users/login");

        http
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        /*
         기본 제공 필터x -> 사용자 직접 구현 하는 필터
         JWT 기반 인증, 커스텀 인증 방식 처리
         */
        //jwt 필터 등록
        http
                .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

        //세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }


}
