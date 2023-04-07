package com.example.demo.comment.controller;

import java.time.LocalDateTime;

import com.example.demo.comment.domain.Comment;

public class CommentApiDto {

    private Long postId;

    private String content;

    private LocalDateTime createdDate;
    private LocalDateTime lastmodifiedDate;

    // todo private 대댓글

    public CommentApiDto(Comment c) {
        this.postId = c.getPost().getId();
        this.content = c.getContent();
        this.createdDate = c.getCreatedDate();
        this.lastmodifiedDate = c.getLastmodifiedDate();
    }

}
