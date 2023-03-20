package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.domain.SiteUser;
import com.example.demo.repository.UserRepository;
import com.example.demo.user.UserRole;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        SiteUser siteUser = this.userRepository.findByusername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        List<GrantedAuthority> authorities = new ArrayList<>();
        if ("admin".equals(username)) {
            // ? 이런식으로 -> authorities.add(new SimpleGrantedAuthority(siteUser.getRole().getDescription()));

            authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getDescription()));
        } else {
            authorities.add(new SimpleGrantedAuthority(UserRole.USER.getDescription()));
        }

        return new User(siteUser.getUsername(), siteUser.getPassword(), authorities);

        // todo 사용자 이름이 admin인 것 보다는, user안에 권한 필드를 만들어서 핸들링하는게 좀 더 바람직 할듯.
        // todo admin 계정은 무조건 1개 만들어놓고, 1개 만들어진 슈퍼계정을 통해 다른 아이디의 권한을 변경하는 방식으로 가야함.
    }

}
