package com.universidad.staytic.controller;

import com.universidad.staytic.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public String notifications(Authentication authentication, Model model) {
        model.addAttribute("notifications", notificationService.findByUserEmail(authentication.getName()));
        return "notifications/list";
    }

    @PostMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Integer id,
                             Authentication authentication,
                             RedirectAttributes redirect) {
        notificationService.markAsRead(id, authentication.getName());
        redirect.addFlashAttribute("success", "Notificacion marcada como leida");
        return "redirect:/notifications";
    }
}
