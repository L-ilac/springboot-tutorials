package com.example.demo.member.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "signed_up")
    private LocalDateTime signedUpDate;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @Column
    private String email;

    @Column
    private String name;

    @Column
    private String nickname;

    @Column
    private String provider;

    @Column
    private String providerId;


}
