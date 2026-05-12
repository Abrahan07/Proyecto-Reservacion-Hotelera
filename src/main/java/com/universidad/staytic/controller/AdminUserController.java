package com.universidad.staytic.controller;

import com.universidad.staytic.dto.UserForm;
import com.universidad.staytic.model.Role;
import com.universidad.staytic.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String email,
                       @RequestParam(required = false) Role role,
                       Model model) {
        model.addAttribute("users", userService.search(name, email, role));
        model.addAttribute("roles", Role.values());
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("selectedRole", role);
        return "admin/users/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("roles", Role.values());
        model.addAttribute("editing", false);
        return "admin/users/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        validatePasswordForCreate(form, result);
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("editing", false);
            return "admin/users/form";
        }
        try {
            userService.createFromAdmin(form);
            redirect.addFlashAttribute("success", "Usuario registrado correctamente");
            return "redirect:/admin/users";
        } catch (RuntimeException ex) {
            result.rejectValue("email", "email.duplicado", ex.getMessage());
            model.addAttribute("roles", Role.values());
            model.addAttribute("editing", false);
            return "admin/users/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        UserForm form = userService.findById(id)
                .map(userService::toForm)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        model.addAttribute("userForm", form);
        model.addAttribute("roles", Role.values());
        model.addAttribute("editing", true);
        return "admin/users/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("userForm") UserForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("roles", Role.values());
            model.addAttribute("editing", true);
            return "admin/users/form";
        }
        try {
            userService.updateFromAdmin(id, form);
            redirect.addFlashAttribute("success", "Usuario actualizado correctamente");
            return "redirect:/admin/users";
        } catch (RuntimeException ex) {
            result.rejectValue("email", "email.duplicado", ex.getMessage());
            model.addAttribute("roles", Role.values());
            model.addAttribute("editing", true);
            return "admin/users/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirect) {
        userService.delete(id);
        redirect.addFlashAttribute("success", "Usuario eliminado correctamente");
        return "redirect:/admin/users";
    }

    private void validatePasswordForCreate(UserForm form, BindingResult result) {
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            result.rejectValue("password", "password.required", "La contraseña es obligatoria");
        }
    }
}
