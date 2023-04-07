package com.example.demo.post.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.example.demo.comment.controller.CommentApiDto;
import com.example.demo.comment.domain.Comment;
import com.example.demo.post.domain.Post;

import lombok.Getter;
import lombok.Setter;

// 특정 글에 대한 정보를 요청했을 때, api 스펙에 맞는 dto 
@Getter
@Setter
public class PostApiDto {

    private Long id;

    private String title;
    private String content;

    private LocalDateTime createdDate;
    private LocalDateTime lastmodifiedDate;

    private int viewCount;

    private int like;

    // todo commnet -> commentApidto 변환 과정 필요
    private List<CommentApiDto> commentList;

    public PostApiDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdDate = post.getCreatedDate();
        this.lastmodifiedDate = post.getLastmodifiedDate();
        this.viewCount = post.getViewCount();
        this.like = post.getLikeCount();
        this.commentList = post.getCommentList().stream().map(c -> new CommentApiDto(c)).collect(Collectors.toList());
    }

}
