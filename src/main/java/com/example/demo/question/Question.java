package com.example.demo.question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.example.demo.answer.Answer;
import com.example.demo.user.SiteUser;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동으로 증가하는 값 -> auto increment
    private Integer id;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    // 질문 1걔 : 답변 여러개 의 형태인 onetomany 관계임을 나타냄. 답변이 여러개이므로 list 형태로 선언
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE) // mappedby 뒤에 들어가는 것은 Answer에서 Question을 참조하기 위해
                                                                    // 선언한 Question 클래스 변수 question임.
    private List<Answer> answerList; // question.getAnswerList() 로 답변목록 접근

    @ManyToOne
    private SiteUser author; // 여러개의 질문(many)이 한명의 사용자(one)에 의해 작성될 수 있음

    @ManyToMany //하 나의 질문에 여러사람이 추천할 수 있고 한 사람이 여러 개의 질문을 추천할 수 있다. 대등한 관계이기 때문에 @ManyToMany를 사용
    Set<SiteUser> voter;
}
