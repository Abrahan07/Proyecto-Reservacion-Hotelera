package com.universidad.staytic.controller;

import com.universidad.staytic.model.User;
import com.universidad.staytic.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLogin() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user,
                           BindingResult result) {
        if (result.hasErrors()) {
            result.getAllErrors().forEach(e -> System.out.println("ERROR: " + e));
            return "auth/register";
        }
        try {
            service.register(user);
            return "redirect:/login?registered";
        } catch (RuntimeException e) {
            result.rejectValue("email", "error.email", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        model.addAttribute("email", auth.getName());
        model.addAttribute("roles", auth.getAuthorities());
        service.findByEmail(auth.getName()).ifPresent(u ->
                model.addAttribute("userName", u.getName())
        );
        return "dashboard";
    }

    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }
}
