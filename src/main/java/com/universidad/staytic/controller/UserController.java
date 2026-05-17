package com.universidad.staytic.controller;

import com.universidad.staytic.dto.ProfileForm;
import com.universidad.staytic.model.User;
import com.universidad.staytic.service.ReservationService;
import com.universidad.staytic.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    private final ReservationService reservationService;
    private final UserService userService;

    public UserController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    @GetMapping("/mis-reservas")
    public String myReservations(Authentication auth, Model model) {
        String email = auth.getName();
        List<com.universidad.staytic.model.Reservation> reservations = reservationService.findReservationsByEmail(email);

        model.addAttribute("reservations", reservations);
        return "reservations/mis-reservas";
    }

    @GetMapping("/mi-perfil")
    public String myProfile(Authentication auth, Model model) {
        String email = auth.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("user", user);
        model.addAttribute("profileForm", userService.toProfileForm(user));
        return "profile/mi-perfil";
    }

    @PostMapping("/mi-perfil")
    public String updateProfile(@Valid @ModelAttribute("profileForm") ProfileForm form,
                                BindingResult result,
                                Authentication auth,
                                Model model,
                                RedirectAttributes redirect,
                                HttpServletRequest request,
                                HttpServletResponse response) {
        User current = userService.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (result.hasErrors()) {
            model.addAttribute("user", current);
            return "profile/mi-perfil";
        }
        boolean emailChanged = !current.getEmail().equalsIgnoreCase(form.getEmail());
        try {
            userService.updateProfile(auth.getName(), form);
            if (emailChanged) {
                new SecurityContextLogoutHandler().logout(request, response, auth);
                return "redirect:/login?profileUpdated";
            }
            redirect.addFlashAttribute("success", "Perfil actualizado correctamente");
            return "redirect:/mi-perfil";
        } catch (RuntimeException ex) {
            result.rejectValue("email", "profile.email", ex.getMessage());
            model.addAttribute("user", current);
            return "profile/mi-perfil";
        }
    }
}
