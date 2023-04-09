package com.example.demo.comment.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.demo.post.domain.Post;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "created")
    private LocalDateTime createdDate;

    @Column(name = "lastmodified")
    private LocalDateTime lastmodifiedDate;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public static Comment createComment(Post post, String content) {
        Comment comment = new Comment();
        comment.content = content;
        comment.createdDate = LocalDateTime.now();

        post.addComment(comment);

        return comment;

    }

    public void setPost(Post post) { // belongTo 와 같은 함수명으로 변경하는 것을 고려.

        this.post = post;
    }

    public void update(String content) {
        this.content = content;
        lastmodifiedDate = LocalDateTime.now();
    }
}
