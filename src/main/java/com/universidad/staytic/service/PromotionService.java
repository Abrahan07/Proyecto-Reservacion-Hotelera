package com.universidad.staytic.service;

import com.universidad.staytic.model.Promotion;
import com.universidad.staytic.repository.PromotionRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public List<Promotion> search(String code, Float discount, LocalDate startDate, LocalDate endDate) {
        return promotionRepository.search(blankToNull(code), discount, startDate, endDate);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public Optional<Promotion> findById(Integer id) {
        return promotionRepository.findById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void save(Promotion promotion) {
        validateDates(promotion);
        if (promotion.getPromotionId() == 0 && promotionRepository.existsByCodeIgnoreCase(promotion.getCode())) {
            throw new RuntimeException("Ya existe una promocion con ese codigo");
        }
        if (promotion.getPromotionId() != 0
                && promotionRepository.existsByCodeIgnoreCaseAndPromotionIdNot(
                promotion.getCode(), promotion.getPromotionId())) {
            throw new RuntimeException("Ya existe otra promocion con ese codigo");
        }
        promotionRepository.save(promotion);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    @Transactional
    public void delete(Integer id) {
        if (!promotionRepository.existsById(id)) {
            throw new RuntimeException("Promocion no encontrada");
        }
        promotionRepository.deleteById(id);
    }

    private void validateDates(Promotion promotion) {
        if (promotion.getStartDate() == null || promotion.getEndDate() == null) {
            throw new RuntimeException("Las fechas de vigencia son obligatorias");
        }
        if (promotion.getEndDate().isBefore(promotion.getStartDate())) {
            throw new RuntimeException("La fecha final no puede ser anterior a la fecha inicial");
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
