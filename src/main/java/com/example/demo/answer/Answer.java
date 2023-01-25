package com.example.demo.answer;

import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.example.demo.question.Question;
import com.example.demo.user.SiteUser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동으로 증가하는 값 -> auto increment
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @ManyToOne // 데이터 베이스 간 관계에서 many to one의 관계 (답변 여러개 : 질문 1개) -> DB상에서는 foreign key 관계가 생성.
    private Question question; // answer.getQuestion().getSubject -> 해당 answer 객체의 질문의 subject를 접근하는 방법

    @ManyToOne
    private SiteUser author;

    @ManyToMany
    Set<SiteUser> voter;
}
