package com.universidad.staytic.repository;

import com.universidad.staytic.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndServiceIdNot(String name, Integer serviceId);
    List<Service> findByAvailableTrueOrderByNameAsc();

    @Query("""
            select s from Service s
            where (:name is null or lower(s.name) like lower(concat('%', :name, '%')))
              and (:price is null or s.price = :price)
              and (:available is null or s.available = :available)
            order by s.name asc
            """)
    List<Service> search(String name, Float price, Boolean available);
}
