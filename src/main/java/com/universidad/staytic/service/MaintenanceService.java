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
        if (roomId == null) {
            throw new RuntimeException("La habitacion es obligatoria");
        }
        Integer maintenanceId = maintenance.getMaintenanceId() == 0 ? null : maintenance.getMaintenanceId();
        Maintenance current = maintenanceId == null ? null : maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
        Room previousRoom = current == null ? null : current.getRoom();

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Habitacion no encontrada"));
        User employee = userRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        maintenance.setRoom(room);
        maintenance.setEmployee(employee);
        if (maintenance.getStatus() == null) {
            maintenance.setStatus(MaintenanceStatus.SCHEDULED);
        }

        if (maintenance.getStatus() == MaintenanceStatus.COMPLETED && maintenance.getCompletedDate() == null) {
            maintenance.setCompletedDate(LocalDateTime.now().withSecond(0).withNano(0));
        }

        Maintenance saved = maintenanceRepository.save(maintenance);
        applyRoomStatusForMaintenance(room, saved.getMaintenanceId());
        if (previousRoom != null && previousRoom.getRoomId() != room.getRoomId()) {
            applyRoomStatusForMaintenance(previousRoom, saved.getMaintenanceId());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void reportIssue(Integer roomId, String description, String employeeEmail) {
        if (description == null || description.isBlank()) {
            throw new RuntimeException("La descripcion del problema es obligatoria");
        }
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
            applyRoomStatusForMaintenance(maintenance.getRoom(), maintenance.getMaintenanceId());
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void delete(Integer id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mantenimiento no encontrado"));
        Room room = maintenance.getRoom();
        maintenanceRepository.delete(maintenance);
        if (room != null) {
            applyRoomStatusForMaintenance(room, id);
        }
    }

    private void applyRoomStatusForMaintenance(Room room, Integer currentMaintenanceId) {
        if (room == null) {
            return;
        }
        long activeMaintenance = maintenanceRepository.countActiveByRoom(room.getRoomId(), currentMaintenanceId);
        Maintenance current = currentMaintenanceId == null ? null : maintenanceRepository.findById(currentMaintenanceId).orElse(null);
        boolean currentIsActive = current != null
                && (current.getStatus() == MaintenanceStatus.SCHEDULED || current.getStatus() == MaintenanceStatus.REPORTED)
                && current.getRoom() != null
                && current.getRoom().getRoomId() == room.getRoomId();

        if (activeMaintenance > 0 || currentIsActive) {
            room.setStatus(RoomStatus.MAINTENANCE);
        } else if (room.getStatus() == RoomStatus.MAINTENANCE) {
            room.setStatus(RoomStatus.AVAILABLE);
        }
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
