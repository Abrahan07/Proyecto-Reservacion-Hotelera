package com.universidad.staytic.controller;

import com.universidad.staytic.model.Room;
import com.universidad.staytic.model.RoomStatus;
import com.universidad.staytic.model.RoomType;
import com.universidad.staytic.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/receptionist/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String number,
                       @RequestParam(required = false) Integer floor,
                       @RequestParam(required = false) RoomType type,
                       @RequestParam(required = false) Float price,
                       @RequestParam(required = false) RoomStatus status,
                       Model model) {
        model.addAttribute("rooms", roomService.search(number, floor, type, price, status));
        addCatalogs(model);
        model.addAttribute("number", number);
        model.addAttribute("floor", floor);
        model.addAttribute("selectedType", type);
        model.addAttribute("price", price);
        model.addAttribute("selectedStatus", status);
        return "rooms/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        Room room = new Room();
        room.setStatus(RoomStatus.AVAILABLE);
        model.addAttribute("room", room);
        model.addAttribute("editing", false);
        addCatalogs(model);
        return "rooms/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("room") Room room,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("editing", false);
            addCatalogs(model);
            return "rooms/form";
        }
        try {
            roomService.save(room);
            redirect.addFlashAttribute("success", "Habitacion registrada correctamente");
            return "redirect:/receptionist/rooms";
        } catch (RuntimeException ex) {
            result.rejectValue("number", "room.number", ex.getMessage());
            model.addAttribute("editing", false);
            addCatalogs(model);
            return "rooms/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        Room room = roomService.findById(id)
                .orElseThrow(() -> new RuntimeException("Habitacion no encontrada"));
        model.addAttribute("room", room);
        model.addAttribute("editing", true);
        addCatalogs(model);
        return "rooms/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id,
                         @Valid @ModelAttribute("room") Room room,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        room.setRoomId(id);
        if (result.hasErrors()) {
            model.addAttribute("editing", true);
            addCatalogs(model);
            return "rooms/form";
        }
        try {
            roomService.save(room);
            redirect.addFlashAttribute("success", "Habitacion actualizada correctamente");
            return "redirect:/receptionist/rooms";
        } catch (RuntimeException ex) {
            result.rejectValue("number", "room.number", ex.getMessage());
            model.addAttribute("editing", true);
            addCatalogs(model);
            return "rooms/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirect) {
        roomService.delete(id);
        redirect.addFlashAttribute("success", "Habitacion eliminada correctamente");
        return "redirect:/receptionist/rooms";
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Integer id,
                               @RequestParam RoomStatus status,
                               RedirectAttributes redirect) {
        roomService.changeStatus(id, status);
        redirect.addFlashAttribute("success", "Estado de habitacion actualizado");
        return "redirect:/receptionist/rooms";
    }

    private void addCatalogs(Model model) {
        model.addAttribute("roomTypes", RoomType.values());
        model.addAttribute("roomStatuses", RoomStatus.values());
    }
}
