package com.example.demo.comment.controller;

import java.time.LocalDateTime;

import com.example.demo.comment.domain.Comment;

import lombok.Getter;

@Getter
// @Setter
public class CommentApiDto {

    private Long id;

    private Long postId;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime lastmodifiedDate;

    // todo private 대댓글

    public CommentApiDto(Comment c) {
        this.id = c.getId();
        this.postId = c.getPost().getId();
        this.content = c.getContent();
        this.createdDate = c.getCreatedDate();
        this.lastmodifiedDate = c.getLastmodifiedDate();
    }

}
