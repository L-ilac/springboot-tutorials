package com.example.demo.comment.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.DataNotFoundException;
import com.example.demo.comment.controller.CommentApiDto;
import com.example.demo.comment.domain.Comment;
import com.example.demo.comment.repository.CommentRepository;
import com.example.demo.post.domain.Post;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    // * 조회 
    // todo (외부에서도 호출할 가능성이 있는 함수인가? -> 아니라면 private 처리)
    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new DataNotFoundException("comment not found"));
    }

    // * 생성
    @Transactional
    public Long newComment(Post post, String content) {
        Comment savedNewComment = commentRepository.save(Comment.createComment(post, content));

        return savedNewComment.getId();
    }

    // * 수정
    @Transactional
    public Long updateComment(Long id, String content) {
        Comment comment = getComment(id); // ! 동일 service의 다른 함수인데 트랜잭션이 어떻게 작동하는지 봐야함
        comment.update(content);

        return comment.getId();
    }

    // * 삭제
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = getComment(id); // ! 동일 service의 다른 함수인데 트랜잭션이 어떻게 작동하는지 봐야함

        commentRepository.delete(comment);
    }

}
