package com.example.demo.domain;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
// @Setter
@Builder(toBuilder = true)
@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    // ! 특정 Entity와 연결된 속성이라는 것을 명시적으로 표시해야함
    @ManyToOne // * 데이터 베이스 간 관계에서 many to one의 관계 (답변 여러개 : 질문 1개) -> DB상에서는 foreign key 관계가 생성.
    private Question question; // * answer.getQuestion().getSubject -> 특정 answer 객체의 질문의 subject를 접근하는 방법

    @ManyToOne // 답변 여러개 : 사용자 1명
    private SiteUser author;

    @ManyToMany
    Set<SiteUser> voter;

    public Answer update(String content) {
        this.content = content;
        this.modifyDate = LocalDateTime.now();

        return this;
    }
}
