package com.example.demo.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.comment.domain.Comment;
import com.example.demo.post.domain.Post;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    public List<Comment> findByPost(Post post);
}