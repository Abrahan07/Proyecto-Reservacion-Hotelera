package com.universidad.staytic.controller;

import com.universidad.staytic.model.Payment;
import com.universidad.staytic.model.PaymentMethod;
import com.universidad.staytic.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/receptionist/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String reservationText,
                       @RequestParam(required = false) Float amount,
                       @RequestParam(required = false) PaymentMethod method,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate paymentDate,
                       Model model) {
        model.addAttribute("payments", paymentService.search(reservationText, amount, method, paymentDate));
        model.addAttribute("methods", PaymentMethod.values());
        model.addAttribute("reservationText", reservationText);
        model.addAttribute("amount", amount);
        model.addAttribute("selectedMethod", method);
        model.addAttribute("paymentDate", paymentDate);
        return "payments/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        Payment payment = new Payment();
        payment.setPaymentDate(LocalDate.now());
        model.addAttribute("payment", payment);
        model.addAttribute("reservationId", null);
        model.addAttribute("editing", false);
        addCatalogs(model);
        return "payments/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("payment") Payment payment,
                         BindingResult result,
                         @RequestParam Integer reservationId,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("reservationId", reservationId);
            model.addAttribute("editing", false);
            addCatalogs(model);
            return "payments/form";
        }
        try {
            paymentService.save(payment, reservationId);
            redirect.addFlashAttribute("success", "Pago registrado correctamente");
            return "redirect:/receptionist/payments";
        } catch (RuntimeException ex) {
            result.reject("payment.error", ex.getMessage());
            model.addAttribute("reservationId", reservationId);
            model.addAttribute("editing", false);
            addCatalogs(model);
            return "payments/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Payment payment = paymentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        model.addAttribute("payment", payment);
        model.addAttribute("reservationId", payment.getReservation().getReservationId());
        model.addAttribute("editing", true);
        addCatalogs(model);
        return "payments/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("payment") Payment payment,
                         BindingResult result,
                         @RequestParam Integer reservationId,
                         Model model,
                         RedirectAttributes redirect) {
        payment.setPaymentId(id);
        if (result.hasErrors()) {
            model.addAttribute("reservationId", reservationId);
            model.addAttribute("editing", true);
            addCatalogs(model);
            return "payments/form";
        }
        try {
            paymentService.save(payment, reservationId);
            redirect.addFlashAttribute("success", "Pago actualizado correctamente");
            return "redirect:/receptionist/payments";
        } catch (RuntimeException ex) {
            result.reject("payment.error", ex.getMessage());
            model.addAttribute("reservationId", reservationId);
            model.addAttribute("editing", true);
            addCatalogs(model);
            return "payments/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirect) {
        paymentService.delete(id);
        redirect.addFlashAttribute("success", "Pago eliminado correctamente");
        return "redirect:/receptionist/payments";
    }

    private void addCatalogs(Model model) {
        model.addAttribute("reservations", paymentService.findReservations());
        model.addAttribute("methods", PaymentMethod.values());
    }
}
