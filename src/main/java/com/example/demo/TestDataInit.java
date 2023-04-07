package com.example.demo;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.demo.comment.domain.Comment;
import com.example.demo.comment.repository.CommentRepository;
import com.example.demo.post.domain.Post;
import com.example.demo.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {

        Post post1 = Post.createPost("Post #1",
                "Cotent of Post #1");
        Post post2 = Post.createPost("Post #2",
                "Cotent of Post #2");
        Post post3 = Post.createPost("Post #3",
                "Cotent of Post #3");

        Comment comment1 = Comment.createComment(post1, "Comment #1 of Post #1");
        Comment comment2 = Comment.createComment(post1, "Comment #2 of Post #1");
        Comment comment3 = Comment.createComment(post2, "Comment #1 of Post #2");
        Comment comment4 = Comment.createComment(post2, "Comment #2 of Post #2");
        Comment comment5 = Comment.createComment(post3, "Comment #1 of Post #3");
        Comment comment6 = Comment.createComment(post3, "Comment #2 of Post #3");
        Comment comment7 = Comment.createComment(post3, "Comment #3 of Post #3");

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);
        commentRepository.save(comment4);
        commentRepository.save(comment5);
        commentRepository.save(comment6);
        commentRepository.save(comment7);

    }
}
