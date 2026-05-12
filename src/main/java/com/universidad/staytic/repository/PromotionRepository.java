package com.universidad.staytic.repository;

import com.universidad.staytic.model.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    Optional<Promotion> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCaseAndPromotionIdNot(String code, Integer promotionId);

    Optional<Promotion> findByCodeIgnoreCaseAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            String code, LocalDate startDate, LocalDate endDate);

    @Query("""
            select p from Promotion p
            where (:code is null or lower(p.code) like lower(concat('%', :code, '%')))
              and (:discount is null or p.discount = :discount)
              and (:startDate is null or p.startDate >= :startDate)
              and (:endDate is null or p.endDate <= :endDate)
            order by p.startDate desc, p.promotionId desc
            """)
    List<Promotion> search(String code, Float discount, LocalDate startDate, LocalDate endDate);
}
