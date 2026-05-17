package com.universidad.staytic.controller;

import com.universidad.staytic.model.User;
import com.universidad.staytic.service.ReservationService;
import com.universidad.staytic.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
        return "profile/mi-perfil";
    }
}
