package com.example.demo.question;

import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.answer.Answer;
import com.example.demo.answer.AnswerForm;
import com.example.demo.user.SiteUser;
import com.example.demo.user.UserService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/question")
@RequiredArgsConstructor // *lombok이 제공하는 애너테이션으로 final이 붙은 속성을 포함하는 생성자를 자동으로 생성하는 역할
@Controller
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        // ! requestparam -> url 에서 특정 변수의 값을 갖고오기 위해 사용. ex) http://~~~~~/list?page=0  일 경우, page=0을 갖고 오기 위해 value="page" 를 명시해야한다. 
        // ! 뒤의 int page와 value ="page"는 맞출 필요 없음. int page는 url의 'page=0'에서 0을 저장하는 용도

        Page<Question> paging = this.questionService.getList(page, keyword);

        // * Model 객체는 따로 생성할 필요없이 컨트롤러 메서드의 매개변수로 지정하기만 하면 스프링부트가 자동으로 Model 객체를 생성한다.
        model.addAttribute("paging", paging); // ! 자바 클래스와 템플릿 간의 연결고리 역할로, 이 변수를 템플릿에서 가져다가 쓸 수 있다.
        model.addAttribute("keyword", keyword);

        return "question_list";

    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) { // * @PathVariable 를 이용해서 각 데이터별로 변하는 값(ex. id)을 url로부터 전달받는다.
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String create(QuestionForm questionForm) {
        return "question_form";
    }

    // * 메서드 오버로딩을 통해 get과 post를 구분할 수 있음
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String create(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/question/list";

        // create 요청을 받을 때, 질문에 대한 정보를 받아와야함.

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) { // * @PathVariable 를 이용해서 각 데이터별로 변하는 값(ex. id)을 url로부터 전달받는다.
        Question question = this.questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");

        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());

        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal,
            @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }

        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }

        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());

        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String delete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);

        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        this.questionService.delete(question);
        return "redirect:/";

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String vote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());

        this.questionService.vote(question, siteUser);

        return String.format("redirect:/question/detail/%s", id);
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