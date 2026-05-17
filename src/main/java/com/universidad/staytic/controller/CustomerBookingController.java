package com.universidad.staytic.controller;

import com.universidad.staytic.dto.CustomerPaymentForm;
import com.universidad.staytic.dto.CustomerReservationForm;
import com.universidad.staytic.model.PaymentMethod;
import com.universidad.staytic.model.Reservation;
import com.universidad.staytic.model.Room;
import com.universidad.staytic.service.CustomerBookingService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/cliente")
public class CustomerBookingController {

    private final CustomerBookingService customerBookingService;

    public CustomerBookingController(CustomerBookingService customerBookingService) {
        this.customerBookingService = customerBookingService;
    }

    @GetMapping("/habitaciones")
    public String rooms(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOut,
                        Model model) {
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        if (checkIn != null && checkOut != null) {
            try {
                model.addAttribute("rooms", customerBookingService.availableRooms(checkIn, checkOut));
            } catch (RuntimeException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        return "customer/rooms";
    }

    @GetMapping("/reservar")
    public String reservationForm(@RequestParam Integer roomId,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOut,
                                  Model model) {
        Room room = customerBookingService.findRoom(roomId)
                .orElseThrow(() -> new RuntimeException("Habitacion no encontrada"));
        CustomerReservationForm form = new CustomerReservationForm();
        form.setRoomId(roomId);
        form.setCheckIn(checkIn);
        form.setCheckOut(checkOut);
        model.addAttribute("reservationForm", form);
        model.addAttribute("room", room);
        model.addAttribute("services", customerBookingService.availableServices());
        return "customer/reservation-form";
    }

    @PostMapping("/reservar")
    public String createReservation(@Valid @ModelAttribute("reservationForm") CustomerReservationForm form,
                                    BindingResult result,
                                    Authentication authentication,
                                    Model model,
                                    RedirectAttributes redirect) {
        Room room = form.getRoomId() == null ? null : customerBookingService.findRoom(form.getRoomId()).orElse(null);
        if (result.hasErrors()) {
            model.addAttribute("room", room);
            model.addAttribute("services", customerBookingService.availableServices());
            return "customer/reservation-form";
        }
        try {
            Reservation reservation = customerBookingService.createReservation(form, authentication.getName());
            redirect.addFlashAttribute("success", "Reservacion creada. Ahora puedes realizar el pago.");
            return "redirect:/cliente/pago/" + reservation.getReservationId();
        } catch (RuntimeException ex) {
            result.reject("reservation.error", ex.getMessage());
            model.addAttribute("room", room);
            model.addAttribute("services", customerBookingService.availableServices());
            return "customer/reservation-form";
        }
    }

    @GetMapping("/pago/{reservationId}")
    public String paymentForm(@PathVariable Integer reservationId,
                              Authentication authentication,
                              Model model) {
        Reservation reservation = customerBookingService.getReservationForUser(reservationId, authentication.getName());
        model.addAttribute("reservation", reservation);
        model.addAttribute("paymentForm", new CustomerPaymentForm());
        model.addAttribute("methods", PaymentMethod.values());
        return "customer/payment";
    }

    @PostMapping("/pago/{reservationId}")
    public String pay(@PathVariable Integer reservationId,
                      @Valid @ModelAttribute("paymentForm") CustomerPaymentForm form,
                      BindingResult result,
                      Authentication authentication,
                      Model model,
                      RedirectAttributes redirect) {
        Reservation reservation = customerBookingService.getReservationForUser(reservationId, authentication.getName());
        if (result.hasErrors()) {
            model.addAttribute("reservation", reservation);
            model.addAttribute("methods", PaymentMethod.values());
            return "customer/payment";
        }
        customerBookingService.payReservation(reservationId, authentication.getName(), form.getPaymentMethod());
        redirect.addFlashAttribute("success", "Pago realizado correctamente. Tu reservacion fue confirmada.");
        return "redirect:/mis-reservas";
    }
}
