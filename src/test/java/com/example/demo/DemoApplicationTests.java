package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.domain.Answer;
import com.example.demo.domain.Question;
import com.example.demo.repository.AnswerRepository;
import com.example.demo.repository.QuestionRepository;
import com.example.demo.service.QuestionService;

@SpringBootTest
class DemoApplicationTests {

	// ! 객체를 스프링이 자동으로 생성하여 주입(DI- Dependency Injection)
	@Autowired // * 테스트 코드에서는 생성자를 통한 객체 주입이 불가능하므로 autowired 사용
	private QuestionRepository questionRepository;

	@Autowired
	private QuestionService questionService;

	@Autowired
	private AnswerRepository answerRepository;

	// ! answerRepository와 questionRepository는 현재 인터페이스만 구현되어있다. 그럼에도 불구하고, 의존주입이 되어 코드가 돌아간다. -> 스프링이 JpaRepository 상속여부를 보고, 실구현체를 만들어 스프링 빈으로 등록했을 가능성이 높음(불확실)

	/* 왜냐하면 Question 리포지터리가 findById를 호출하여 Question 객체를 조회하고 나면 DB세션이 끊어지기 때문이다. 그 이후에 실행되는 q.getAnswerList() 메서드는 세션이 종료되어 오류가 발생한다. 
	답변 데이터 리스트는 q 객체를 조회할때 가져오지 않고 q.getAnswerList() 메서드를 호출하는 시점에 가져오기 때문이다. 이렇게 필요한 시점에 데이터를 가져오는 방식을 Lazy 방식이라고 한다. 
	이와 반대로 q 객체를 조회할때 답변 리스트를 모두 가져오는 방식은 Eager 방식이라고 한다. 
	@OneToMany, @ManyToOne 애너테이션의 옵션으로 fetch=FetchType.LAZY 또는 fetch=FetchType.EAGER 처럼 가져오는 방식을 설정할 수 있는데 이 책에서는 따로 지정하지 않고 항상 디폴트 값을 사용할 것이다.
	사실 이 문제는 테스트 코드에서만 발생한다. 실제 서버에서 JPA 프로그램들을 실행할 때는 DB 세션이 종료되지 않기 때문에 위와 같은 오류가 발생하지 않는다.
	테스트 코드를 수행할 때 위와 같은 오류를 방지할 수 있는 가장 간단한 방법은 다음처럼 @Transactional 애너테이션을 사용하는 것이다. 
	@Transactional 애너테이션을 사용하면 메서드가 종료될 때까지 DB 세션이 유지된다. */

	@Test
	void test() {
		for (int i = 0; i < 300; i++) {
			String subject = String.format("테스트데이터입니다: [%03d]", i);
			String content = "내용없음";

			this.questionService.create(subject, content, null);
		}
	}

	@Transactional // * 위의 설명을 꼭 다시 읽어볼 것. 장고에서도 lazyobject와 같은 개념들이 있었는데 유사한 개념인거 같다. 
	@Test
	void testJpa() {

		Optional<Question> oq4 = this.questionRepository.findById(2);
		assertTrue(oq4.isPresent());
		Question q7 = oq4.get();

		List<Answer> aList = q7.getAnswerList();
		assertEquals(1, aList.size());
		assertEquals("WADADA", aList.get(0).getContent());

		// ===========================================================================================================================================================
		// 답변 조회 + 답변을 통해 어떤 질문이었는지 찾기
		Optional<Answer> oa = this.answerRepository.findById(1);
		assertTrue(oa.isPresent());
		Answer tmpa = oa.get();
		assertEquals(2, tmpa.getQuestion().getId()); // ! 답변하나에 연결된 질문은 하나라서 그냥 엔티티 속성 접근하듯 접근하면 된다.

		// ===========================================================================================================================================================
		// 답변 등록
		Optional<Question> tmpoq = this.questionRepository.findById(2);
		assertTrue(tmpoq.isPresent());
		Question q6 = tmpoq.get();

		Answer a = Answer.builder()
				.content("WADADA")
				.question(q6)
				.createDate(LocalDateTime.now())
				.build();

		this.answerRepository.save(a);

		// ===========================================================================================================================================================
		// 질문 삭제
		assertEquals(2, this.questionRepository.count()); // * count함수는 해당 리포지터리의 총 데이터 갯수를 리턴한다. 
		Optional<Question> oq2 = this.questionRepository.findById(1);
		assertTrue(oq2.isPresent());
		Question q5 = oq2.get();
		this.questionRepository.delete(q5); // * 실제 데이터 베이스에 있는 데이터를 삭제하는 과정
		assertEquals(1, this.questionRepository.count());

		// ===========================================================================================================================================================
		// 질문 수정

		Optional<Question> oq1 = this.questionRepository.findById(1);
		assertTrue(oq1.isPresent()); // * 주어진 값이 참인지 테스트
		Question q4 = oq1.get();
		// q4.setSubject("좋아하는 여자가수는?");
		this.questionRepository.save(q4); // *데이터 수정 후 저장해야 실제 데이터베이스에 반영됌

		// ===========================================================================================================================================================
		// 질문 조회 by Subject Like (특정 문자열을 포함하는 데이터 조회)
		// * %문자열, 문자열%, %문자열% -> 문자열로 시작, 문자열로 종료, 문자열을 포함
		List<Question> qList = this.questionRepository.findBySubjectLike("%가수%"); // 가수를 포함하는 문자열로 검색한 경우
		Question tmpq = qList.get(0);
		assertEquals("좋아하는 가수는?", tmpq.getSubject());

		// ===========================================================================================================================================================
		// 질문 조회 by Subject and Content (두 개의 속성을 동시에 조건으로 사용)
		Question q1 = this.questionRepository.findBySubjectAndContent("좋아하는 가수는?", "가수의 이름을 알려주세요.");
		assertEquals(1, q1.getId());

		// ===========================================================================================================================================================
		// 질문 조회 by Subject
		// ! QuestionRepository 인터페이스에 findBySubject 함수를 선언하면, JPA가 해당 메서드명을 분석하여 쿼리를 만들고 실행한다.
		// * findBy + 엔티티의 속성명 으로 함수를 작성하면 됌.
		Question q0 = this.questionRepository.findBySubject("좋아하는 가수는?");
		assertEquals(1, q0.getId());

		// ===========================================================================================================================================================
		// 질문 조회 by ID
		// id값으로 조회는 findById를 사용한다. 리턴 타입이 question이 아닌 optional 이다.
		// * Optional은 null 처리를 유연하게 하기 위해서 사용하는 클래스이고, isPresent 를 이용해서 null 확인후 실제 question 객체값을 얻음.
		Optional<Question> oq = this.questionRepository.findById(1);

		if (oq.isPresent()) {
			Question q = oq.get();
			assertEquals("좋아하는 가수는?", q.getSubject());
		}
		// ===========================================================================================================================================================
		// 질문 조회
		// List<Question> all = this.questionRepository.findAll(); // 모든 질문 다 갖고 오기(id 순서대로 갖고오기)
		// assertEquals(6, all.size());

		// Question q = all.get(0); // List 자료구조에서 인덱스로 갖고오기
		// * junit 의 테스트 함수 assertEquals(예상값, 실제값) -> 일치하면 테스트 통과 일치하지않으면 테스트 실패
		// assertEquals("좋아하는 가수는?", q.getSubject());

		// ===========================================================================================================================================================
		// 질문 저장
		// Question q1 = new Question();
		// q1.setSubject("좋아하는 가수는?");
		// q1.setContent("가수의 이름을 알려주세요.");
		// q1.setCreateDate(LocalDateTime.now());

		// this.questionRepository.save(q1);

		// Question q2 = new Question();
		// q2.setSubject("좋아하는 노래는?");
		// q2.setContent("노래의 제목을 알려주세요.");
		// q2.setCreateDate(LocalDateTime.now());

		// this.questionRepository.save(q2);

	}

}
