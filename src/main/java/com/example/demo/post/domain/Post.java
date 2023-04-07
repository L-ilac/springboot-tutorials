package com.example.demo.post.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.comment.domain.Comment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "created")
    private LocalDateTime createdDate;

    @Column(name = "lastmodified")
    private LocalDateTime lastmodifiedDate;

    @Column(name = "view", nullable = false)
    private int viewCount;

    @Column(name = "likeCount", nullable = false) // ! primitive int nullable test
    private int likeCount; // todo 좋아요는 유저당 1번으로 제한하려면 유저 엔티티와 매핑해야함

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> commentList = new ArrayList<>();

    public static Post createPost(String title, String content) {
        Post post = new Post();
        post.title = title;
        post.content = content;
        post.createdDate = LocalDateTime.now();

        return post;
    }

    public void addComment(Comment comment) {
        this.commentList.add(comment);
        comment.setPost(this);

    }

    public void update(String content) {
        this.content = content;
        lastmodifiedDate = LocalDateTime.now();
    }

}