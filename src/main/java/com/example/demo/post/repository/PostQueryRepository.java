package com.example.demo.post.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.demo.post.domain.Post;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {

    private final EntityManager em;

    public Optional<Post> findById(Long postId) {
        return Optional
                .ofNullable(em.createQuery("select p from Post p join fetch Comment", Post.class).getSingleResult());

    }

}
