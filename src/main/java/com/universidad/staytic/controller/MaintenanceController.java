package com.universidad.staytic.controller;

import com.universidad.staytic.model.Maintenance;
import com.universidad.staytic.model.MaintenanceStatus;
import com.universidad.staytic.service.MaintenanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/receptionist/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String roomText,
                       @RequestParam(required = false) String type,
                       @RequestParam(required = false) MaintenanceStatus status,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
                       Model model) {
        model.addAttribute("items", maintenanceService.search(roomText, type, status, fromDate, toDate));
        model.addAttribute("statuses", MaintenanceStatus.values());
        model.addAttribute("roomText", roomText);
        model.addAttribute("type", type);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);
        return "maintenance/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        Maintenance maintenance = new Maintenance();
        maintenance.setStatus(MaintenanceStatus.SCHEDULED);
        model.addAttribute("maintenance", maintenance);
        model.addAttribute("roomId", null);
        model.addAttribute("rooms", maintenanceService.findRooms());
        model.addAttribute("statuses", MaintenanceStatus.values());
        model.addAttribute("editing", false);
        return "maintenance/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("maintenance") Maintenance maintenance,
                         BindingResult result,
                         @RequestParam Integer roomId,
                         Authentication authentication,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("roomId", roomId);
            model.addAttribute("rooms", maintenanceService.findRooms());
            model.addAttribute("statuses", MaintenanceStatus.values());
            model.addAttribute("editing", false);
            return "maintenance/form";
        }
        try {
            maintenanceService.save(maintenance, roomId, authentication.getName());
            redirect.addFlashAttribute("success", "Mantenimiento registrado correctamente");
            return "redirect:/receptionist/maintenance";
        } catch (RuntimeException ex) {
            result.reject("maintenance.error", ex.getMessage());
            model.addAttribute("roomId", roomId);
            model.addAttribute("rooms", maintenanceService.findRooms());
            model.addAttribute("statuses", MaintenanceStatus.values());
            model.addAttribute("editing", false);
            return "maintenance/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Maintenance maintenance = maintenanceService.findById(id)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
        model.addAttribute("maintenance", maintenance);
        model.addAttribute("roomId", maintenance.getRoom().getRoomId());
        model.addAttribute("rooms", maintenanceService.findRooms());
        model.addAttribute("statuses", MaintenanceStatus.values());
        model.addAttribute("editing", true);
        return "maintenance/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("maintenance") Maintenance maintenance,
                         BindingResult result,
                         @RequestParam Integer roomId,
                         Authentication authentication,
                         Model model,
                         RedirectAttributes redirect) {
        maintenance.setMaintenanceId(id);
        if (result.hasErrors()) {
            model.addAttribute("roomId", roomId);
            model.addAttribute("rooms", maintenanceService.findRooms());
            model.addAttribute("statuses", MaintenanceStatus.values());
            model.addAttribute("editing", true);
            return "maintenance/form";
        }
        try {
            maintenanceService.save(maintenance, roomId, authentication.getName());
            redirect.addFlashAttribute("success", "Mantenimiento actualizado correctamente");
            return "redirect:/receptionist/maintenance";
        } catch (RuntimeException ex) {
            result.reject("maintenance.error", ex.getMessage());
            model.addAttribute("roomId", roomId);
            model.addAttribute("rooms", maintenanceService.findRooms());
            model.addAttribute("statuses", MaintenanceStatus.values());
            model.addAttribute("editing", true);
            return "maintenance/form";
        }
    }

    @GetMapping("/report")
    public String reportForm(Model model) {
        model.addAttribute("rooms", maintenanceService.findRooms());
        model.addAttribute("roomId", null);
        model.addAttribute("description", "");
        return "maintenance/report";
    }

    @PostMapping("/report")
    public String reportIssue(@RequestParam Integer roomId,
                              @RequestParam @NotBlank String description,
                              Authentication authentication,
                              RedirectAttributes redirect) {
        maintenanceService.reportIssue(roomId, description, authentication.getName());
        redirect.addFlashAttribute("success", "Problema reportado correctamente");
        return "redirect:/receptionist/maintenance";
    }

    @PostMapping("/{id}/complete")
    public String complete(@PathVariable Integer id, RedirectAttributes redirect) {
        maintenanceService.complete(id);
        redirect.addFlashAttribute("success", "Mantenimiento finalizado correctamente");
        return "redirect:/receptionist/maintenance";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirect) {
        maintenanceService.delete(id);
        redirect.addFlashAttribute("success", "Mantenimiento eliminado correctamente");
        return "redirect:/receptionist/maintenance";
    }
}
