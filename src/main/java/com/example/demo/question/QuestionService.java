package com.example.demo.question;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.example.demo.DataNotFoundException;
import com.example.demo.answer.Answer;
import com.example.demo.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> getList() {
        return this.questionRepository.findAll();
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, SiteUser author) {
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());
        question.setAuthor(author);
        // question.setAnswerList(null);

        this.questionRepository.save(question);
    }

    public Page<Question> getList(int page, String keyword) {
        List<Sort.Order> sorts = new ArrayList<>(); // ! 정렬 기준을 담는 리스트
        sorts.add(Sort.Order.desc("createDate")); // ! 정렬기준으로 (작성일시+역순) 을 넣고,
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts)); // ! sorts에 들어가있는 정렬기준을 토대로, pagerequest에 들어가있는 question 값들을 정렬함
        Specification<Question> spec = search(keyword);
        return this.questionRepository.findAll(spec, pageable);
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());

        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {

        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    private Specification<Question> search(String keyword) {
        return new Specification<>() {
            private static final long serialVersionID = 1L;

            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                query.distinct(true);
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return criteriaBuilder.or(criteriaBuilder.like(q.get("subject"), "%" + keyword + "%"),
                        criteriaBuilder.like(q.get("content"), "%" + keyword + "%"),
                        criteriaBuilder.like(u1.get("username"), "%" + keyword + "%"),
                        criteriaBuilder.like(a.get("content"), "%" + keyword + "%"),
                        criteriaBuilder.like(u2.get("username"), "%" + keyword + "%"));
            }
        };
    }
}

// ! Question, Answer 대신 사용할 DTO(Data Transfer Object) 클래스가 필요하다. 
// ! 서비스를 만들어 놓음으로써, 불필요한 반복 구현을 제거함으로써 모듈화의 장점을 얻을 수 있고, 보안적으로도 안전하다. (클래스의 상속을 쓰는 이유와 유사함)