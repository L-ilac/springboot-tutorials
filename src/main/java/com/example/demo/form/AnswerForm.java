package com.example.demo.form;

import java.time.LocalDateTime;

import com.example.demo.domain.Answer;

import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerForm {

    @NotEmpty(message = "내용은 필수 항목입니다.")
    private String content;

    public Answer dtoToEntity() {
        Answer answer = Answer.builder()
                .content(this.getContent())
                .createDate(LocalDateTime.now())
                .build();

        return answer;

    }

}
