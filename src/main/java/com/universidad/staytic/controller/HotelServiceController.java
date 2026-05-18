package com.universidad.staytic.controller;

import com.universidad.staytic.model.Service;
import com.universidad.staytic.service.HotelServiceService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/receptionist/services")
public class HotelServiceController {

    private final HotelServiceService hotelServiceService;

    public HotelServiceController(HotelServiceService hotelServiceService) {
        this.hotelServiceService = hotelServiceService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String name,
                       @RequestParam(required = false) Float price,
                       @RequestParam(required = false) Boolean available,
                       Model model) {
        model.addAttribute("services", hotelServiceService.search(name, price, available));
        model.addAttribute("name", name);
        model.addAttribute("price", price);
        model.addAttribute("selectedAvailable", available);
        return "services/list";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createForm(Model model) {
        Service service = new Service();
        service.setAvailable(true);
        model.addAttribute("service", service);
        model.addAttribute("editing", false);
        return "services/form";
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String create(@Valid @ModelAttribute("service") Service service,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("editing", false);
            return "services/form";
        }
        try {
            hotelServiceService.save(service);
            redirect.addFlashAttribute("success", "Servicio registrado correctamente");
            return "redirect:/receptionist/services";
        } catch (RuntimeException ex) {
            result.reject("service.error", ex.getMessage());
            model.addAttribute("editing", false);
            return "services/form";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editForm(@PathVariable Integer id, Model model) {
        Service service = hotelServiceService.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));
        model.addAttribute("service", service);
        model.addAttribute("editing", true);
        return "services/form";
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("service") Service service,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        service.setServiceId(id);
        if (result.hasErrors()) {
            model.addAttribute("editing", true);
            return "services/form";
        }
        try {
            hotelServiceService.save(service);
            redirect.addFlashAttribute("success", "Servicio actualizado correctamente");
            return "redirect:/receptionist/services";
        } catch (RuntimeException ex) {
            result.reject("service.error", ex.getMessage());
            model.addAttribute("editing", true);
            return "services/form";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Integer id, RedirectAttributes redirect) {
        hotelServiceService.delete(id);
        redirect.addFlashAttribute("success", "Servicio eliminado correctamente");
        return "redirect:/receptionist/services";
    }
}
