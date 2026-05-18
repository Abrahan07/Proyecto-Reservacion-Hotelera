package com.universidad.staytic.controller;

import com.universidad.staytic.dto.ProfileForm;
import com.universidad.staytic.model.User;
import com.universidad.staytic.service.CustomerBookingService;
import com.universidad.staytic.service.ReservationService;
import com.universidad.staytic.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    private final ReservationService reservationService;
    private final UserService userService;
    private final CustomerBookingService customerBookingService;

    public UserController(ReservationService reservationService,
                          UserService userService,
                          CustomerBookingService customerBookingService) {
        this.reservationService = reservationService;
        this.userService = userService;
        this.customerBookingService = customerBookingService;
    }

    @GetMapping("/mis-reservas")
    @PreAuthorize("hasRole('GUEST')")
    public String myReservations(Authentication auth, Model model) {
        String email = auth.getName();
        List<com.universidad.staytic.model.Reservation> reservations = reservationService.findReservationsByEmail(email);

        model.addAttribute("reservations", reservations);
        return "reservations/mis-reservas";
    }

    @PostMapping("/mis-reservas/{id}/eliminar")
    @PreAuthorize("hasRole('GUEST')")
    public String deleteMyReservation(@PathVariable Integer id,
                                      Authentication auth,
                                      RedirectAttributes redirect) {
        try {
            customerBookingService.deleteReservationForUser(id, auth.getName());
            redirect.addFlashAttribute("success", "Reservacion eliminada correctamente");
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/mis-reservas";
    }

    @GetMapping("/mi-perfil")
    @PreAuthorize("hasRole('GUEST')")
    public String myProfile(Authentication auth, Model model) {
        String email = auth.getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        model.addAttribute("user", user);
        model.addAttribute("profileForm", userService.toProfileForm(user));
        return "profile/mi-perfil";
    }

    @PostMapping("/mi-perfil")
    @PreAuthorize("hasRole('GUEST')")
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
