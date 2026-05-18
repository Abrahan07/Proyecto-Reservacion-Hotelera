package com.universidad.staytic.controller;

import com.universidad.staytic.dto.ReservationForm;
import com.universidad.staytic.model.ReservationStatus;
import com.universidad.staytic.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/receptionist/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String userText,
                       @RequestParam(required = false) String roomText,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOut,
                       @RequestParam(required = false) ReservationStatus status,
                       Model model) {
        model.addAttribute("reservations", reservationService.search(userText, roomText, checkIn, checkOut, status));
        model.addAttribute("statuses", ReservationStatus.values());
        model.addAttribute("userText", userText);
        model.addAttribute("roomText", roomText);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        model.addAttribute("selectedStatus", status);
        return "reservations/list";
    }

    @GetMapping("/new")
    public String createForm(@RequestParam(required = false) Integer roomId,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
                             @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOut,
                             Model model) {
        ReservationForm form = new ReservationForm();
        if (roomId != null) {
            form.setRoomIds(List.of(roomId));
        }
        form.setCheckIn(checkIn);
        form.setCheckOut(checkOut);
        model.addAttribute("reservationForm", form);
        addCatalogs(model);
        model.addAttribute("editing", false);
        return "reservations" +
                "/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("reservationForm") ReservationForm form,
                         BindingResult result,
                         Authentication authentication,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            addCatalogs(model);
            model.addAttribute("editing", false);
            return "reservations/form";
        }
        try {
            reservationService.create(form, authentication.getName());
            redirect.addFlashAttribute("success", "Reservacion creada correctamente");
            return "redirect:/receptionist/reservations";
        } catch (RuntimeException ex) {
            result.reject("reservation.error", ex.getMessage());
            addCatalogs(model);
            model.addAttribute("editing", false);
            return "reservations/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        ReservationForm form = reservationService.findById(id)
                .map(reservationService::toForm)
                .orElseThrow(() -> new RuntimeException("Reservacion no encontrada"));
        model.addAttribute("reservationForm", form);
        addCatalogs(model);
        model.addAttribute("editing", true);
        return "reservations/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("reservationForm") ReservationForm form,
                         BindingResult result,
                         Authentication authentication,
                         Model model,
                         RedirectAttributes redirect) {
        form.setReservationId(id);
        if (result.hasErrors()) {
            addCatalogs(model);
            model.addAttribute("editing", true);
            return "reservations/form";
        }
        try {
            reservationService.update(id, form, authentication.getName());
            redirect.addFlashAttribute("success", "Reservacion actualizada correctamente");
            return "redirect:/receptionist/reservations";
        } catch (RuntimeException ex) {
            result.reject("reservation.error", ex.getMessage());
            addCatalogs(model);
            model.addAttribute("editing", true);
            return "reservations/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            reservationService.delete(id);
            redirect.addFlashAttribute("success", "Reservacion eliminada correctamente");
        } catch (RuntimeException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/receptionist/reservations";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Integer id,
                               @RequestParam ReservationStatus status,
                               RedirectAttributes redirect) {
        reservationService.changeStatus(id, status);
        redirect.addFlashAttribute("success", "Estado de reservacion actualizado");
        return "redirect:/receptionist/reservations";
    }

    @GetMapping("/availability")
    public String availability(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOut,
                               Model model) {
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        if (checkIn != null && checkOut != null) {
            try {
                model.addAttribute("rooms", reservationService.availableRooms(checkIn, checkOut));
            } catch (RuntimeException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        return "reservations/availability";
    }

    private void addCatalogs(Model model) {
        model.addAttribute("users", reservationService.findUsers());
        model.addAttribute("rooms", reservationService.findRooms());
        model.addAttribute("statuses", ReservationStatus.values());
    }
}
