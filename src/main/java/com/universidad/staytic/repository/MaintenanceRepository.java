package com.universidad.staytic.repository;

import com.universidad.staytic.model.Maintenance;
import com.universidad.staytic.model.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {

    @Query("""
            select m from Maintenance m
            where (:roomText is null or lower(m.room.number) like lower(concat('%', :roomText, '%')))
              and (:type is null or lower(m.type) like lower(concat('%', :type, '%')))
              and (:status is null or m.status = :status)
              and (:fromDate is null or m.scheduledDate >= :fromDate)
              and (:toDate is null or m.scheduledDate <= :toDate)
            order by m.scheduledDate desc, m.maintenanceId desc
            """)
    List<Maintenance> search(String roomText, String type, MaintenanceStatus status,
                             LocalDateTime fromDate, LocalDateTime toDate);
}
