
package com.example.demo.post.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.post.domain.Post;
import com.example.demo.post.service.PostService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/board")
@RestController
@RequiredArgsConstructor
public class PostApiController {

    private final PostService postService;

    // todo 페이징이 적용되지 않아서, 그냥 모든 post를 다 갖고옴. 추후에 적용해야함
    @GetMapping
    public List<PostApiDto> list() {
        List<Post> posts = postService.getAllPosts();
        return posts.stream().map(p -> new PostApiDto(p)).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public PostApiDto getById(@PathVariable Long id) {
        Post post = postService.getPostWithComment(id);
        return new PostApiDto(post);

    }

    @PostMapping
    public PostApiDto create(@RequestBody PostCreateDto createDto) {
        Long id = postService.newPost(createDto.getTitle(), createDto.getContent());

        Post post = postService.getOnlyPost(id);

        return new PostApiDto(post);
    }

    @PatchMapping("/{id}")
    public PostApiDto update(@PathVariable Long id, @RequestBody PostUpdateDto updateDto) {
        postService.updatePost(id, updateDto.getContent());
        Post post = postService.getOnlyPost(id);

        return new PostApiDto(post);
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        postService.deletePost(id);

        return "Post id : " + id + " 삭제완료";

    }

}
