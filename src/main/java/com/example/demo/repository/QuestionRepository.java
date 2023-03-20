package com.example.demo.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Question;

// * JpaRepository<엔티티 타입, 엔티티의 pk속성 타입> -> 이 repository로 db에서 조회할 데이터는 Question이라는 것을 명시(JpaRepository를 생성하기 위한 규칙)
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    Question findBySubject(String subject);
    // * 인터페이스에 선언된 함수는 원래, 이를 implements하는 클래스에서 필수적으로 기능을 구현해야한다.
    // ! 스프링의 프록시 패턴에 의해 QuestionRepository 인터페이스의 실구현체가 만들어지고, 그 안에서 위의 함수도 같이 구현된다.

    Question findBySubjectAndContent(String subject, String content); // * 여러개의 속성을 동시에 조건으로 적용하고 싶을 땐, And 를 사용한다.

    // ! 리포지터리의 메서드명은 데이터를 조회하는 쿼리문의 where 조건을 결정하는 역할을 한다
    // ? 예시들
    // *  And	            findBySubjectAndContent(String subject, String content)	                여러 컬럼을 and 로 검색
    // *  Or	            findBySubjectOrContent(String subject, String content)	                여러 컬럼을 or 로 검색
    // *  Between	        findByCreateDateBetween(LocalDateTime fromDate, LocalDateTime toDate)	컬럼을 between으로 검색
    // *  LessThan	        findByIdLessThan(Integer id)	                                        작은 항목 검색
    // *  GreaterThanEqual	findByIdGreaterThanEqual(Integer id)	                                    크거나 같은 항목 검색
    // *  Like	            findBySubjectLike(String subject)	                                    like 검색
    // *  In	            findBySubjectIn(String[] subjects)	                                    여러 값중에 하나인 항목 검색
    // *  OrderBy	        findBySubjectOrderByCreateDateAsc(String subject)	                    검색 결과를 정렬하여 전달
    // ! 단, 응답의 결과가 여러건일 경우에는 메서드의 리턴타입을 List<Entity> 형태로 해야한다.

    List<Question> findBySubjectLike(String subject); // *Like 의 경우 특정 문자열이 포함되어 있는 데이터를 찾는 용도

    Page<Question> findAll(Pageable pageable);

    Page<Question> findAll(Specification<Question> spec, Pageable pageable);

}
