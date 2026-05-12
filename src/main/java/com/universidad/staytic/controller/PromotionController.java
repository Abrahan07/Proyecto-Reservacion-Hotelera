package com.universidad.staytic.controller;

import com.universidad.staytic.model.Promotion;
import com.universidad.staytic.service.PromotionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/receptionist/promotions")
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String code,
                       @RequestParam(required = false) Float discount,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                       Model model) {
        model.addAttribute("promotions", promotionService.search(code, discount, startDate, endDate));
        model.addAttribute("code", code);
        model.addAttribute("discount", discount);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        return "promotions/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("promotion", new Promotion());
        model.addAttribute("editing", false);
        return "promotions/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("promotion") Promotion promotion,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("editing", false);
            return "promotions/form";
        }
        try {
            promotionService.save(promotion);
            redirect.addFlashAttribute("success", "Promocion registrada correctamente");
            return "redirect:/receptionist/promotions";
        } catch (RuntimeException ex) {
            result.reject("promotion.error", ex.getMessage());
            model.addAttribute("editing", false);
            return "promotions/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Promotion promotion = promotionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Promocion no encontrada"));
        model.addAttribute("promotion", promotion);
        model.addAttribute("editing", true);
        return "promotions/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("promotion") Promotion promotion,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        promotion.setPromotionId(id);
        if (result.hasErrors()) {
            model.addAttribute("editing", true);
            return "promotions/form";
        }
        try {
            promotionService.save(promotion);
            redirect.addFlashAttribute("success", "Promocion actualizada correctamente");
            return "redirect:/receptionist/promotions";
        } catch (RuntimeException ex) {
            result.reject("promotion.error", ex.getMessage());
            model.addAttribute("editing", true);
            return "promotions/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirect) {
        promotionService.delete(id);
        redirect.addFlashAttribute("success", "Promocion eliminada correctamente");
        return "redirect:/receptionist/promotions";
    }
}
