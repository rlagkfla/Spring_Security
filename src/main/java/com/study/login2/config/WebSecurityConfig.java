package com.study.login2.config;

import com.study.login2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity // Security 사용
@RequiredArgsConstructor
@Configuration
//@ConditionalOnDefaultWebSecurity
//@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebSecurityConfig {
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure(){ // static 하위 파일 목록(css, js, img) 인증 무시

        return (web) -> web.ignoring().requestMatchers("/static/css/**", "/static/js/**", "/static/img/**");

    }


    @Bean
//    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{ // http 관련 인증 설정

        // 사이트 위변조 요청 방지
        http.csrf().disable();

        //http.authorizeHttpRequests().antMatchers
        http.authorizeHttpRequests() // 접근에 대한 인증 설정
                .requestMatchers("/login","/signup","/user").permitAll() // 누구나 접근 허용
                .requestMatchers("/").hasRole("USER") // USER, ADMIN만 접근 가능
                .requestMatchers("/admin").hasRole("ADMIN") // ADMIN만 접근 가능
                .anyRequest().authenticated() // 나머지 요청들은 권한의 종류에 상관 없이 권한이 있어야 접근 가능
            .and()
                .formLogin()
                    .loginPage("/login") // 로그인 페이지 링크
                    .defaultSuccessUrl("/") // 로그인 성공 후 리다이렉트 주소 -> main.html
                    .permitAll()
            .and()
                .logout()
                    .logoutSuccessUrl("/login") // 로그아웃 성공시 리다이렉트 주소
                    .invalidateHttpSession(true); // 세션 날리기


//        http.formLogin()
//                .loginPage("/login")
//                .defaultSuccessUrl("/")
//            .and().logout()
//                .logoutSuccessUrl("/login")
//                .invalidateHttpSession(true);


        return http.build();

    }

    @Bean
    public PasswordEncoder PasswordEncoder(){ // 필요한 정보들을 가져오는 곳

        return new BCryptPasswordEncoder();
        // 해당 서비스(userService)에서는 UserDetailsService를 implements해서 loadUserByUsername() 구현해야함 (서비스 참고)
    }


}
