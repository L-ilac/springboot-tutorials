// package com.example.demo.config;

// import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     // private final CustomOAuth2UserService customOAuth2UserService;

//     // public SecurityConfig(CustomOAuth2UserService customOAuth2UserService) {
//     //     this.customOAuth2UserService = customOAuth2UserService;
//     // }

//     @Bean
//     public BCryptPasswordEncoder encoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//         http.csrf().disable()
//                 .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                 .and()
//                 // .headers()
//                 // .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN))
//                 // .and()
//                 .formLogin().disable()
//                 .httpBasic().disable()
//                 .authorizeHttpRequests()
//                 .requestMatchers("/api/user").permitAll()
//                 .and()
//                 // .authorizeHttpRequests((authorize) -> authorize.requestMatchers(PathRequest.toH2Console()).permitAll())
//                 .oauth2Login().userInfoEndpoint().userService(customOAuth2UserService);

//         return http.build();
//     }

// }
