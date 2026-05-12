package com.universidad.staytic.service;

import com.universidad.staytic.model.Room;
import com.universidad.staytic.model.RoomStatus;
import com.universidad.staytic.model.RoomType;
import com.universidad.staytic.repository.RoomRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Room> search(String number, Integer floor, RoomType type, Float price, RoomStatus status) {
        return roomRepository.search(blankToNull(number), floor, type, price, status);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public Optional<Room> findById(Integer id) {
        return roomRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void save(Room room) {
        if (room.getRoomId() == 0 && roomRepository.existsByNumber(room.getNumber())) {
            throw new RuntimeException("Ya existe una habitacion con ese numero");
        }
        if (room.getRoomId() != 0 && roomRepository.existsByNumberAndRoomIdNot(room.getNumber(), room.getRoomId())) {
            throw new RuntimeException("Ya existe otra habitacion con ese numero");
        }
        roomRepository.save(room);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void delete(Integer id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Habitacion no encontrada");
        }
        roomRepository.deleteById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void changeStatus(Integer id, RoomStatus status) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habitacion no encontrada"));
        room.setStatus(status);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
