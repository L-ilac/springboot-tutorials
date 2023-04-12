package com.example.demo.member.controller;


import com.example.demo.member.domain.Member;
import com.example.demo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/member")
@Controller
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/profile/{id}")
    public MemberProfileDto profile(@PathVariable Long id){
        Member member = memberService.getMember(id);
        return new MemberProfileDto(member);
    }
}
