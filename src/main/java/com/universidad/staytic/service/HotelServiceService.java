package com.universidad.staytic.service;

import com.universidad.staytic.model.Service;
import com.universidad.staytic.repository.ServiceRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class HotelServiceService {

    private final ServiceRepository serviceRepository;

    public HotelServiceService(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Service> search(String name, Float price, Boolean available) {
        return serviceRepository.search(blankToNull(name), price, available);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public Optional<Service> findById(Integer id) {
        return serviceRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void save(Service service) {
        if (service.getServiceId() == 0 && serviceRepository.existsByNameIgnoreCase(service.getName())) {
            throw new RuntimeException("Ya existe un servicio con ese nombre");
        }
        if (service.getServiceId() != 0
                && serviceRepository.existsByNameIgnoreCaseAndServiceIdNot(service.getName(), service.getServiceId())) {
            throw new RuntimeException("Ya existe otro servicio con ese nombre");
        }
        serviceRepository.save(service);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void delete(Integer id) {
        if (!serviceRepository.existsById(id)) {
            throw new RuntimeException("Servicio no encontrado");
        }
        serviceRepository.deleteById(id);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
