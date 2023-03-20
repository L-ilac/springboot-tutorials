package com.example.demo.form;

import com.example.demo.domain.Question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
    @NotEmpty(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 200)
    private String subject;

    @NotEmpty(message = "내용은 필수 입력 항목입니다.")
    private String content;

    public Question dtoToEntity() {
        Question question = Question.builder()
                .subject(this.getSubject())
                .content(this.getContent())
                .build();

        return question;

    }
}
