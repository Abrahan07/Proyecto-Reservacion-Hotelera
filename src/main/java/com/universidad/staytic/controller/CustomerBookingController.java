package com.universidad.staytic.controller;

import com.universidad.staytic.dto.CustomerPaymentForm;
import com.universidad.staytic.dto.CustomerReservationForm;
import com.universidad.staytic.model.PaymentMethod;
import com.universidad.staytic.model.Promotion;
import com.universidad.staytic.model.Reservation;
import com.universidad.staytic.model.Room;
import com.universidad.staytic.service.CustomerBookingService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cliente")
public class CustomerBookingController {

    private static final String PENDING_RESERVATION = "pendingCustomerReservation";

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
                                    HttpSession session,
                                    RedirectAttributes redirect) {
        Room room = form.getRoomId() == null ? null : customerBookingService.findRoom(form.getRoomId()).orElse(null);
        if (result.hasErrors()) {
            model.addAttribute("room", room);
            model.addAttribute("services", customerBookingService.availableServices());
            return "customer/reservation-form";
        }
        try {
            customerBookingService.validateReservationRequest(form);
            session.setAttribute(PENDING_RESERVATION, form);
            return "redirect:/cliente/pago/confirmar";
        } catch (RuntimeException ex) {
            result.reject("reservation.error", ex.getMessage());
            model.addAttribute("room", room);
            model.addAttribute("services", customerBookingService.availableServices());
            return "customer/reservation-form";
        }
    }

    @GetMapping("/pago/confirmar")
    public String paymentPreview(HttpSession session, Model model) {
        CustomerReservationForm form = (CustomerReservationForm) session.getAttribute(PENDING_RESERVATION);
        if (form == null) {
            return "redirect:/dashboard";
        }
        Room room = customerBookingService.findRoom(form.getRoomId())
                .orElseThrow(() -> new RuntimeException("Habitacion no encontrada"));
        addPaymentPreview(model, form, room);
        model.addAttribute("paymentForm", new CustomerPaymentForm());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("pendingPayment", true);
        return "customer/payment";
    }

    @PostMapping("/pago/confirmar")
    public String confirmPayment(@Valid @ModelAttribute("paymentForm") CustomerPaymentForm paymentForm,
                                 BindingResult result,
                                 HttpSession session,
                                 Authentication authentication,
                                 Model model,
                                 RedirectAttributes redirect) {
        CustomerReservationForm form = (CustomerReservationForm) session.getAttribute(PENDING_RESERVATION);
        if (form == null) {
            return "redirect:/dashboard";
        }
        if (result.hasErrors()) {
            Room room = customerBookingService.findRoom(form.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Habitacion no encontrada"));
            addPaymentPreview(model, form, room);
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("pendingPayment", true);
            return "customer/payment";
        }
        customerBookingService.createAndPayReservation(form, authentication.getName(), paymentForm.getPaymentMethod());
        session.removeAttribute(PENDING_RESERVATION);
        redirect.addFlashAttribute("success", "Pago confirmado y reservacion creada correctamente.");
        return "redirect:/mis-reservas";
    }

    @PostMapping("/pago/anular")
    public String cancelPendingPayment(HttpSession session, RedirectAttributes redirect) {
        CustomerReservationForm form = (CustomerReservationForm) session.getAttribute(PENDING_RESERVATION);
        session.removeAttribute(PENDING_RESERVATION);
        redirect.addFlashAttribute("success", "Pago anulado. No se creo ninguna reservacion.");
        if (form != null) {
            return "redirect:/dashboard?checkIn=" + form.getCheckIn() + "&checkOut=" + form.getCheckOut();
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/pago/{reservationId}")
    public String paymentForm(@PathVariable Integer reservationId,
                              Authentication authentication,
                              Model model) {
        Reservation reservation = customerBookingService.getReservationForUser(reservationId, authentication.getName());
        model.addAttribute("reservation", reservation);
        model.addAttribute("paymentForm", new CustomerPaymentForm());
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("pendingPayment", false);
        return "customer/payment";
    }

    @PostMapping("/pago/{reservationId}")
    public String pay(@PathVariable Integer reservationId,
                      @Valid @ModelAttribute("paymentForm") CustomerPaymentForm paymentForm,
                      BindingResult result,
                      Authentication authentication,
                      Model model,
                      RedirectAttributes redirect) {
        if (result.hasErrors()) {
            Reservation reservation = customerBookingService.getReservationForUser(reservationId, authentication.getName());
            model.addAttribute("reservation", reservation);
            model.addAttribute("paymentMethods", PaymentMethod.values());
            model.addAttribute("pendingPayment", false);
            return "customer/payment";
        }
        customerBookingService.payReservation(reservationId, authentication.getName(), paymentForm.getPaymentMethod());
        redirect.addFlashAttribute("success", "Pago realizado correctamente. Tu reservacion fue confirmada.");
        return "redirect:/mis-reservas";
    }

    @PostMapping("/pago/{reservationId}/anular")
    public String cancelPayment(@PathVariable Integer reservationId,
                                Authentication authentication,
                                RedirectAttributes redirect) {
        customerBookingService.cancelReservation(reservationId, authentication.getName());
        redirect.addFlashAttribute("success", "Pago anulado y reservacion cancelada.");
        return "redirect:/mis-reservas";
    }

    @GetMapping("/promocion")
    @ResponseBody
    public Map<String, Object> promotion(@RequestParam String code,
                                         @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkIn) {
        Map<String, Object> response = new HashMap<>();
        try {
            Promotion promotion = customerBookingService.getValidPromotion(code, checkIn);
            response.put("valid", promotion != null);
            response.put("discount", promotion == null ? 0 : promotion.getDiscount());
            response.put("code", promotion == null ? "" : promotion.getCode());
        } catch (RuntimeException ex) {
            response.put("valid", false);
            response.put("discount", 0);
            response.put("message", ex.getMessage());
        }
        return response;
    }

    private void addPaymentPreview(Model model, CustomerReservationForm form, Room room) {
        float discount = 0;
        try {
            Promotion promotion = customerBookingService.getValidPromotion(form.getPromotionCode(), form.getCheckIn());
            discount = promotion == null ? 0 : promotion.getDiscount();
        } catch (RuntimeException ignored) {
            discount = 0;
        }
        long nights = Math.max(1, ChronoUnit.DAYS.between(form.getCheckIn(), form.getCheckOut()));
        float roomTotal = room.getPricePerNight() * nights;
        float serviceTotal = customerBookingService.availableServices().stream()
                .filter(service -> form.getServiceIds() != null && form.getServiceIds().contains(service.getServiceId()))
                .map(com.universidad.staytic.model.Service::getPrice)
                .reduce(0f, Float::sum);
        float discountAmount = roomTotal * (discount / 100);
        model.addAttribute("reservationForm", form);
        model.addAttribute("room", room);
        model.addAttribute("nights", nights);
        model.addAttribute("roomTotal", roomTotal);
        model.addAttribute("serviceTotal", serviceTotal);
        model.addAttribute("discount", discount);
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("total", roomTotal - discountAmount + serviceTotal);
    }
}
