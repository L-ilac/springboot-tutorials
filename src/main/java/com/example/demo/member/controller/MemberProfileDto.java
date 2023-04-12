package com.example.demo.member.controller;

import com.example.demo.member.domain.Member;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
//@Setter
public class MemberProfileDto {

    private Long id;
    private String nickname;
    private LocalDateTime signedUpDate;
    private String provider;
    private String email;

    public MemberProfileDto(Member member) {
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.signedUpDate = member.getSignedUpDate();
        this.provider = member.getProvider();
        this.email = member.getEmail();
    }
}
