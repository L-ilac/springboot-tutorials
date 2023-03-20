package com.example.demo.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
// @Setter
@Entity // ! JPA 에서 데이터 접근을 객체로 하기 위해서 필요한 애너테이션이라고 생각하면 될듯
public class Question {

    @Id // * primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // * 자동으로 증가하는 값 -> auto increment
    private Integer id;

    @Column(length = 200) // * db table의 column에 들어갈 데이터 길이 설정 ex. varchar(length)
    private String subject;

    @Column(columnDefinition = "TEXT") // * "내용"처럼 글자 수를 제한할 수 없는 경우에 사용
    private String content;

    // * 카멜케이스의 변수는 모두 소문자로 변경되고 _(언더바)로 단어가 구분되어 변환
    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    @Column
    private Integer viewCount;

    // * 질문 1개 : 답변 여러개 의 형태인 onetomany 관계임을 나타냄. 답변이 여러개이므로 list 형태로 선언
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE) // ! mappedby 값은 Answer에서 Question을 참조하기 위해 선언한 Question 클래스 필드변수의 이름 question임.
    private List<Answer> answerList; // * question.getAnswerList() 로 답변목록 접근

    @ManyToOne
    private SiteUser author; // 여러개의 질문(many)이 한명의 사용자(one)에 의해 작성될 수 있음

    @ManyToMany //하나의 질문에 여러사람이 추천할 수 있고 한 사람이 여러 개의 질문을 추천할 수 있다. 대등한 관계이기 때문에 @ManyToMany를 사용
    Set<SiteUser> voter;

    public Question update(String subject, String content) {
        this.subject = subject;
        this.content = content;
        this.modifyDate = LocalDateTime.now();

        return this;
    }

    public void addViewCount() {
        this.viewCount++;
    }
}

// ! 엔티티의 속성은 @Column 애너테이션을 사용하지 않더라도 테이블 컬럼으로 인식한다. 컬럼의 세부설정을 위해 @Column을 사용한다.
// ! 테이블 컬럼으로 인식하고 싶지 않은 경우에만 @Transient 애너테이션을 사용한다. 