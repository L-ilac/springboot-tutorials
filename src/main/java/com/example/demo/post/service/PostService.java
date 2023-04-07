package com.example.demo.post.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.DataNotFoundException;
import com.example.demo.post.domain.Post;
import com.example.demo.post.repository.PostQueryRepository;
import com.example.demo.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PostService { // * 일단 osiv를 키고 + controller 에서 entity -> dto 변환

    private final PostRepository postRepository;
    private final PostQueryRepository postQueryRepository;

    // * 조회
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // LAZY Loading으로 Post 갖고 오기
    public Post getOnlyPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new DataNotFoundException("post not found"));
    }

    // 페치 조인으로 Post 와 Comment 까지 전부 갖고오기
    public Post getPostWithComment(Long postId) {
        return postQueryRepository.findById(postId).orElseThrow(() -> new DataNotFoundException("post not found"));
    }

    // * 생성
    @Transactional
    public Long newPost(String title, String content) {
        Post savedNewPost = postRepository.save(Post.createPost(title, content));
        return savedNewPost.getId();

    }

    // * 수정
    @Transactional
    public Long updatePost(Long postId, String content) {
        Post post = getOnlyPost(postId); // ! 동일 service의 다른 함수인데 트랜잭션이 어떻게 작동하는지 봐야함
        post.update(content);
        return post.getId();

    }

    // * 삭제
    @Transactional
    public void deletePost(Long postId) {
        Post post = getOnlyPost(postId); // ! 동일 service의 다른 함수인데 트랜잭션이 어떻게 작동하는지 봐야함
        postRepository.delete(post);
    }

}
