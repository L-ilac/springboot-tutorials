package com.example.demo.controller;

import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.form.UserCreateForm;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signup(@ModelAttribute UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword().equals(userCreateForm.getPasswordcheck())) {
            bindingResult.rejectValue("passwordcheck", "passwordINCORRECT", "2개의 패스워드가 일치하지 않습니다.");

            return "signup_form";

        }
        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword());

        } catch (DataIntegrityViolationException e) {
            log.info("error", e);
            bindingResult.reject("duplicateUsername", "이미 등록된 사용자입니다.");
            return "signup_form";
        } catch (Exception e) {
            log.info("error", e);
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    // todo 시큐리티의 로그인이 실패할 경우에는 로그인 페이지로 다시 리다이렉트 된다. 이 때 페이지 파라미터로 error가 함께 전달된다.(파라미터 확인)
    @GetMapping("/login")
    public String login() {
        return "login_form";
    }
}
