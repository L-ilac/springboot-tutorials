package com.example.demo.controller;

import java.net.URLEncoder;
import java.security.Principal;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.domain.Answer;
import com.example.demo.domain.Question;
import com.example.demo.domain.SiteUser;
import com.example.demo.form.AnswerForm;
import com.example.demo.service.AnswerService;
import com.example.demo.service.QuestionService;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;

    @PreAuthorize("isAuthenticated()") // ! 이 애너테이션을 사용하면, 이 url로 요청이 들어오면 자동으로 로그인 화면으로 이동하고, 로그인 후에는 원래 화면으로 리다이렉트된다. 
    @PostMapping("/create/{id}")
    public String createAnswer(Model model, @PathVariable Integer id,
            @Valid @ModelAttribute AnswerForm answerForm,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes, Principal principal) {

        Question question = this.questionService.getQuestion(id); // * 서비스 계층에서 질문이 없을 경우 예외를 던짐

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            // todo 답변을 작성하는데 실패했을 때, url이 그대로 /create/{id} 인가?(question/detail/id 여야함) + redirect가 필요 없는 이유?
            return "question_detail";
        }

        SiteUser siteUser = this.userService.getUser(principal.getName());
        Answer createdAnswer = this.answerService.create(question, answerForm.getContent(), siteUser);

        // ? this.answerService.create(question, answerForm, siteUser);

        redirectAttributes.addAttribute("question_id", id);
        redirectAttributes.addAttribute("id", createdAnswer.getId());
        return "redirect:/question/detail/{question_id}#answer_{id}";

        // URLEncoder.encode("redirect:/question/detail/%s#answer_%s", null);
        // return String.format(, id, answer.getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modifyAnswer(@ModelAttribute AnswerForm answerForm, Principal principal,
            @PathVariable Integer id) {
        Answer answer = this.answerService.getAnswer(id);

        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        answerForm.setContent(answer.getContent());

        return "answer_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modifyAnswer(@Valid @ModelAttribute AnswerForm answerForm, BindingResult bindingResult,
            RedirectAttributes redirectAttribute,
            Principal principal,
            @PathVariable Integer id) {

        if (bindingResult.hasErrors()) {
            return "answer_form";
        }

        Answer answer = this.answerService.getAnswer(id);

        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        this.answerService.modify(answer, answerForm);

        redirectAttribute.addAttribute("question_id", answer.getQuestion().getId());

        return "redirect:/question/detail/{question_id}#answer_{id}";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(RedirectAttributes redirectAttributes, Principal principal, @PathVariable Integer id) {
        Answer answer = this.answerService.getAnswer(id);

        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        this.answerService.delete(answer);
        redirectAttributes.addAttribute("id", answer.getQuestion().getId());

        return "redirect:/question/detail/{}";

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String vote(RedirectAttributes redirectAttributes, Principal principal, @PathVariable Integer id) {

        SiteUser siteUser = this.userService.getUser(principal.getName());
        Answer answer = this.answerService.vote(id, siteUser);

        redirectAttributes.addAttribute("question_id", answer.getQuestion().getId());
        return "redirect:/question/detail/%{question_id}#answer_%{id}";
    }

}
