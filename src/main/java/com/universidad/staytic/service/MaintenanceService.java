package com.universidad.staytic.service;

import com.universidad.staytic.model.*;
import com.universidad.staytic.repository.MaintenanceRepository;
import com.universidad.staytic.repository.RoomRepository;
import com.universidad.staytic.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository,
                              RoomRepository roomRepository,
                              UserRepository userRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Maintenance> search(String roomText, String type, MaintenanceStatus status,
                                    LocalDate fromDate, LocalDate toDate) {
        return maintenanceRepository.search(
                blankToNull(roomText),
                blankToNull(type),
                status,
                startOfDay(fromDate),
                endOfDay(toDate));
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public Optional<Maintenance> findById(Integer id) {
        return maintenanceRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Room> findRooms() {
        return roomRepository.findAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void save(Maintenance maintenance, Integer roomId, String employeeEmail) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Habitacion no encontrada"));
        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        maintenance.setRoom(room);
        maintenance.setEmployee(employee);
        if (maintenance.getStatus() == null) {
            maintenance.setStatus(MaintenanceStatus.SCHEDULED);
        }
        if (maintenance.getStatus() == MaintenanceStatus.SCHEDULED
                || maintenance.getStatus() == MaintenanceStatus.REPORTED) {
            room.setStatus(RoomStatus.MAINTENANCE);
        }
        maintenanceRepository.save(maintenance);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void reportIssue(Integer roomId, String description, String employeeEmail) {
        Maintenance maintenance = new Maintenance();
        maintenance.setType("PROBLEMA");
        maintenance.setDescription(description);
        maintenance.setScheduledDate(LocalDateTime.now().withSecond(0).withNano(0));
        maintenance.setStatus(MaintenanceStatus.REPORTED);
        save(maintenance, roomId, employeeEmail);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void complete(Integer id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
        maintenance.setCompletedDate(LocalDateTime.now().withSecond(0).withNano(0));
        maintenance.setStatus(MaintenanceStatus.COMPLETED);
        if (maintenance.getRoom() != null) {
            maintenance.getRoom().setStatus(RoomStatus.AVAILABLE);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void delete(Integer id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new RuntimeException("Mantenimiento no encontrado");
        }
        maintenanceRepository.deleteById(id);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private LocalDateTime startOfDay(LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    private LocalDateTime endOfDay(LocalDate date) {
        return date == null ? null : date.atTime(23, 59, 59);
    }
}
