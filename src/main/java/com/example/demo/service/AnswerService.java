package com.example.demo.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.DataNotFoundException;
import com.example.demo.domain.Answer;
import com.example.demo.domain.Question;
import com.example.demo.domain.SiteUser;
import com.example.demo.form.AnswerForm;
import com.example.demo.repository.AnswerRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;

    public Answer create(Question question, String content, SiteUser author) {
        Answer answer = Answer.builder()
                .content(content)
                .createDate(LocalDateTime.now())
                .question(question)
                .author(author)
                .build();

        this.answerRepository.save(answer);
        return answer;
    }

    // todo builder 패턴을 이용한 객체 생성
    public void create(Question question, AnswerForm answerForm, SiteUser siteUser) {
        Answer answer = answerForm.dtoToEntity();
        answer.toBuilder()
                .question(question)
                .author(siteUser)
                .build();

        this.answerRepository.save(answer);
    }

    public Answer getAnswer(Integer id) {

        return this.answerRepository.findById(id).orElseThrow(() -> new DataNotFoundException("answer not found"));
    }

    public Page<Answer> getList(int pageNum) {
        List<Sort.Order> sortPolicy = new ArrayList<>();

        // todo 정렬 기준의 다양화
        sortPolicy.add(Sort.Order.desc("modifyDate"));

        Pageable pageable = PageRequest.of(pageNum, 10, Sort.by(sortPolicy));
        return this.answerRepository.findAll(pageable);
    }

    public void modify(Answer answer, AnswerForm answerForm) {
        answer.update(answerForm.getContent());

        this.answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }

    public Answer vote(Integer answer_id, SiteUser siteUser) {
        Answer answer = getAnswer(answer_id);
        answer.getVoter().add(siteUser);

        return this.answerRepository.save(answer);
    }

}
