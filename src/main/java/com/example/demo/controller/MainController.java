package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/")
    public String root() {
        return "redirect:/question/list";
        // * redirect:<URL> - URL로 리다이렉트 (리다이렉트는 완전히 새로운 URL로 요청이 된다.)
        // * forward:<URL> - URL로 포워드 (포워드는 기존 요청 값들이 유지된 상태로 URL이 전환된다.)
    }
}
