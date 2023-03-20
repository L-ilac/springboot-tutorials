package com.example.demo.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.domain.SiteUser;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/profile")
@RequiredArgsConstructor
@Controller
public class ProfileController {

    private final UserService userService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String profileDetail(Model model, @PathVariable Long id, Principal principal) {
        SiteUser user = this.userService.getUser(principal.getName());

        if (!user.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "열람 권한이 없습니다.");
        }

        model.addAttribute("user", user);
        return "profile_detail";
    }

}
