package com.example.demo.post.controller;

import lombok.Getter;

// post 생성에 사용되는 dto

@Getter
public class PostCreateDto {
    String title;

    String content;
}
