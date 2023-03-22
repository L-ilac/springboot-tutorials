package com.example.demo.form;

import java.time.LocalDateTime;

import com.example.demo.domain.Comment;

import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDto {

    @NotEmpty(message = "내용은 필수 항목입니다.")
    private String content;

    public Comment dtoToEntity() {
        Comment answer = Comment.builder()
                .content(this.getContent())
                .createDate(LocalDateTime.now())
                .build();

        return answer;

    }

}
