package com.example.demo.comment.controller;

import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.comment.domain.Comment;
import com.example.demo.comment.service.CommentService;
import com.example.demo.post.domain.Post;
import com.example.demo.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;
    private final PostService postService;

    @PostMapping("/comment/{id}")
    public CommentApiDto create(@PathVariable Long postId, @RequestBody CommentCreateDto createDto) {

        Post post = postService.getOnlyPost(postId);
        Long id = commentService.newComment(post, createDto.getContent());

        Comment comment = commentService.getComment(id);
        return new CommentApiDto(comment);
    }

    @PatchMapping("comment/{id}")
    public CommentApiDto update(@PathVariable Long id, @RequestBody CommentUpdateDto updateDto) {
        commentService.updateComment(id, updateDto.getContent());
        Comment comment = commentService.getComment(id);

        return new CommentApiDto(comment);

    }

    @DeleteMapping("comment/{id}")
    public String delete(@PathVariable Long id) {
        commentService.deleteComment(id);

        return "Comment id : " + id + " 삭제완료";
    }

}
