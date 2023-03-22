package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.example.demo.DataNotFoundException;
import com.example.demo.domain.Comment;
import com.example.demo.domain.Post;
import com.example.demo.domain.SiteUser;
import com.example.demo.form.PostDto;
import com.example.demo.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository questionRepository;

    // * 모든 질문을 전부 가져오는 getList
    public List<Post> getList() {
        return this.questionRepository.findAll();
    }

    // * 정수 타입의 페이지번호를 입력받아 해당 페이지의 질문 목록을 가져오는 getList
    public Page<Post> getList(int pageNum, String keyword) {
        List<Sort.Order> sortPolicy = new ArrayList<>(); // ! 정렬 기준을 담는 리스트
        sortPolicy.add(Sort.Order.desc("createDate")); // ! 정렬기준으로 (작성일시+역순) 을 넣고,
        Pageable pageable = PageRequest.of(pageNum, 10, Sort.by(sortPolicy)); // ! 정렬기준을 설정하여, PageRequest 생성

        // todo develop 다양한 정렬기준(조회수, 추천수, 사전순 등등) 적용해볼 것 

        Specification<Post> spec = search(keyword);
        return this.questionRepository.findAll(spec, pageable);
    }

    public Post getQuestion(Integer id) {

        return this.questionRepository.findById(id).orElseThrow(() -> new DataNotFoundException("question not found"));
    }

    // todo builder 패턴을 이용해서 객체를 저장하는 것이 과연 성능적으로 좋은가?
    public void create(PostDto questionForm, SiteUser siteUser) {
        Post question = questionForm.dtoToEntity();
        question.toBuilder()
                .author(siteUser)
                .createDate(LocalDateTime.now())
                .build();

        this.questionRepository.save(question);
    }

    public Post create(String subject, String content, SiteUser author) {
        Post question = Post.builder()
                .content(content)
                .subject(subject)
                .author(author)
                .createDate(LocalDateTime.now())
                .viewCount(0)
                .build();

        return this.questionRepository.save(question);
    }

    public void modify(Post question, PostDto questionForm) {
        question.update(questionForm.getSubject(), questionForm.getContent());
        this.questionRepository.save(question);
    }

    public void delete(Post question) {
        this.questionRepository.delete(question);
    }

    public void vote(Integer question_id, SiteUser siteUser) {
        Post question = getQuestion(question_id);
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    private Specification<Post> search(String keyword) {
        return new Specification<>() {
            private static final long serialVersionID = 1L;

            @Override
            public Predicate toPredicate(Root<Post> q, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                query.distinct(true);
                Join<Post, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Post, Comment> a = q.join("answerList", JoinType.LEFT);
                Join<Comment, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return criteriaBuilder.or(criteriaBuilder.like(q.get("subject"), "%" + keyword + "%"),
                        criteriaBuilder.like(q.get("content"), "%" + keyword + "%"),
                        criteriaBuilder.like(u1.get("username"), "%" + keyword + "%"),
                        criteriaBuilder.like(a.get("content"), "%" + keyword + "%"),
                        criteriaBuilder.like(u2.get("username"), "%" + keyword + "%"));
            }
        };
    }

    public void addViewCount(Integer id) {
        Post question = getQuestion(id);
        question.addViewCount();

        this.questionRepository.save(question);
    }

}

// ! Question, Answer 대신 사용할 DTO(Data Transfer Object) 클래스가 필요하다. 
// ! 서비스를 만들어 놓음으로써, 불필요한 반복 구현을 제거함으로써 모듈화의 장점을 얻을 수 있고, 보안적으로도 안전하다. (클래스의 상속을 쓰는 이유와 유사함)