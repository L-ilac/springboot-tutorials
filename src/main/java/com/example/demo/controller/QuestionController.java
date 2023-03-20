package com.example.demo.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
// import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.domain.Answer;
import com.example.demo.domain.Question;
import com.example.demo.domain.SiteUser;
import com.example.demo.form.AnswerForm;
import com.example.demo.form.QuestionForm;
import com.example.demo.service.AnswerService;
import com.example.demo.service.QuestionService;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/question")
@RequiredArgsConstructor // *lombok이 제공하는 애너테이션으로 final이 붙은 클래스의 필드 속성을 포함하는 생성자를 자동으로 생성하는 역할
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "keyword", defaultValue = "") String keyword, HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        log.info("session = {}", session);

        Page<Question> paging = this.questionService.getList(page, keyword);
        // * paging에서 객체 꺼내는법 -> List<Question> contents = paging.getContent();

        // * Model 객체는 따로 생성할 필요없이 컨트롤러 메서드의 매개변수로 지정하기만 하면 스프링부트가 자동으로 Model 객체를 생성한다.
        model.addAttribute("paging", paging); // ! 자바 클래스와 템플릿 간의 연결고리 역할로, 이 변수를 템플릿에서 가져다가 쓸 수 있다.
        model.addAttribute("keyword", keyword);

        return "question_list";

    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable Integer id, @ModelAttribute AnswerForm answerForm,
            @RequestParam(defaultValue = "0") int page) { // * @PathVariable 를 이용해서 각 데이터별로 변하는 값(ex. id)을 url로부터 전달받는다.
        // ! AnswerForm 이 필요한 이유 -> 템플릿에서 th:object를 사용하기 때문에, 빈 객체라도 던져줘야한다.

        // todo 중복조회 카운트 문제
        this.questionService.addViewCount(id);
        Question question = this.questionService.getQuestion(id);

        // Page<Answer> paging = this.answerService.getList(page);

        model.addAttribute("question", question);
        // model.addAttribute("paging", paging);

        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(@ModelAttribute QuestionForm questionForm) {
        return "question_form";
    }

    // * 메서드 오버로딩을 통해 get과 post를 구분할 수 있음
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String create(@Valid @ModelAttribute QuestionForm questionForm, BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Principal principal) {

        if (bindingResult.hasErrors()) {
            // todo 답변을 작성하는데 실패했을 때, url이 그대로인가? + redirect가 필요 없는 이유?
            return "question_form";
        }

        SiteUser siteUser = this.userService.getUser(principal.getName());

        Question createdQuestion = this.questionService.create(questionForm.getSubject(), questionForm.getContent(),
                siteUser);

        // todo builder performance? -> this.questionService.create(questionForm, siteUser);

        redirectAttributes.addAttribute("id", createdQuestion.getId());

        return "redirect:/question/detail/{id}";

        // create 요청을 받을 때, 질문에 대한 정보를 받아와야함.

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(@ModelAttribute QuestionForm questionForm, @PathVariable Integer id, Principal principal) { // * @PathVariable 를 이용해서 각 데이터별로 변하는 값(ex. id)을 url로부터 전달받는다.
        Question question = this.questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            // todo 예외로 던지는게 맞는걸까?
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");

        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());

        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(@Valid @ModelAttribute QuestionForm questionForm, BindingResult bindingResult,
            Principal principal,
            @PathVariable Integer id) {

        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        Question question = this.questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            // todo 예외로 던지는게 맞는걸까?
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }

        this.questionService.modify(question, questionForm);

        return "redirect:/question/detail/{id}";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(Principal principal, @PathVariable Integer id) {
        Question question = this.questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }

        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String vote(Principal principal, @PathVariable Integer id) {

        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(id, siteUser);

        return "redirect:/question/detail/{id}";
    }

}

// !스프링의 의존성 주입(Dependency Injection) 방식 3가지
// *@Autowired 속성 - 속성에 @Autowired 애너테이션을 적용하여 객체를 주입하는 방식
// *생성자 - 생성자를 작성하여 객체를 주입하는 방식 (권장하는 방식) 
// *Setter - Setter 메서드를 작성하여 객체를 주입하는 방식 (메서드에 @Autowired 애너테이션 적용이 필요하다.

//? 자바 for each 문
//? for (type var: iterate) { body-of-loop }

// * PathVariable은 @RequestParam과 다르게 default 값을 설정하지 않으므로 만약 default 값이 필요한 조회 요청을 한다면 @RequestParam을 사용하여 구현하면 되고, 
// * 앞서 보여주었던 예제와 같이 id와 같이 필수로 입력되어야 하는 값이라면 @PathVariable을 사용하여 구현하면 될 것으로 생각됩니다.

// todo -> 작성자 추가하기, 수정과 등록 폼 분리하기