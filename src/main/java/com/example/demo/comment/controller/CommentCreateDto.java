package com.example.demo.comment.controller;

import lombok.Getter;

@Getter
public class CommentCreateDto {

    Long postId;
    String content;
}
