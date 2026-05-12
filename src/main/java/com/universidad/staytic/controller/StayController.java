package com.universidad.staytic.controller;

import com.universidad.staytic.dto.CheckInForm;
import com.universidad.staytic.dto.CheckOutForm;
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
import java.time.LocalDateTime;

@Controller
@RequestMapping("/receptionist")
public class StayController {

    private final ReservationService reservationService;

    public StayController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/checkins")
    public String checkIns(@RequestParam(required = false) String roomText,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
                           @RequestParam(required = false) String employeeText,
                           Model model) {
        model.addAttribute("checkins", reservationService.searchCheckIns(roomText, fromDate, toDate, employeeText));
        model.addAttribute("roomText", roomText);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("employeeText", employeeText);
        return "stays/checkins";
    }

    @GetMapping("/checkins/new")
    public String checkInForm(@RequestParam(required = false) Integer reservationId, Model model) {
        CheckInForm form = new CheckInForm();
        form.setReservationId(reservationId);
        form.setCheckInDateTime(LocalDateTime.now().withSecond(0).withNano(0));
        model.addAttribute("checkInForm", form);
        model.addAttribute("reservations", reservationService.findReservableForCheckIn());
        return "stays/checkin-form";
    }

    @PostMapping("/checkins")
    public String registerCheckIn(@Valid @ModelAttribute("checkInForm") CheckInForm form,
                                  BindingResult result,
                                  Authentication authentication,
                                  Model model,
                                  RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("reservations", reservationService.findReservableForCheckIn());
            return "stays/checkin-form";
        }
        try {
            reservationService.registerCheckIn(form, authentication.getName());
            redirect.addFlashAttribute("success", "Check-in registrado correctamente");
            return "redirect:/receptionist/checkins";
        } catch (RuntimeException ex) {
            result.reject("checkin.error", ex.getMessage());
            model.addAttribute("reservations", reservationService.findReservableForCheckIn());
            return "stays/checkin-form";
        }
    }

    @GetMapping("/checkouts")
    public String checkOuts(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
                            @RequestParam(required = false) Float additionalCharges,
                            @RequestParam(required = false) Float penalty,
                            @RequestParam(required = false) String roomText,
                            Model model) {
        model.addAttribute("checkouts", reservationService.searchCheckOuts(fromDate, toDate, additionalCharges, penalty, roomText));
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        model.addAttribute("additionalCharges", additionalCharges);
        model.addAttribute("penalty", penalty);
        model.addAttribute("roomText", roomText);
        return "stays/checkouts";
    }

    @GetMapping("/checkouts/new")
    public String checkOutForm(@RequestParam(required = false) Integer reservationId, Model model) {
        CheckOutForm form = new CheckOutForm();
        form.setReservationId(reservationId);
        form.setCheckOutDateTime(LocalDateTime.now().withSecond(0).withNano(0));
        model.addAttribute("checkOutForm", form);
        model.addAttribute("reservations", reservationService.findReservableForCheckOut());
        return "stays/checkout-form";
    }

    @PostMapping("/checkouts")
    public String registerCheckOut(@Valid @ModelAttribute("checkOutForm") CheckOutForm form,
                                   BindingResult result,
                                   Authentication authentication,
                                   Model model,
                                   RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("reservations", reservationService.findReservableForCheckOut());
            return "stays/checkout-form";
        }
        try {
            reservationService.registerCheckOut(form, authentication.getName());
            redirect.addFlashAttribute("success", "Check-out registrado correctamente");
            return "redirect:/receptionist/checkouts";
        } catch (RuntimeException ex) {
            result.reject("checkout.error", ex.getMessage());
            model.addAttribute("reservations", reservationService.findReservableForCheckOut());
            return "stays/checkout-form";
        }
    }
}
