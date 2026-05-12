package com.universidad.staytic.repository;

import com.universidad.staytic.model.Reservation;
import com.universidad.staytic.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("""
            select distinct r from Reservation r
            join r.details d
            where (:userText is null or lower(r.user.name) like lower(concat('%', :userText, '%'))
                   or lower(r.user.email) like lower(concat('%', :userText, '%')))
              and (:roomText is null or lower(d.room.number) like lower(concat('%', :roomText, '%')))
              and (:checkIn is null or d.scheduledCheckIn = :checkIn)
              and (:checkOut is null or d.scheduledCheckOut = :checkOut)
              and (:status is null or r.status = :status)
            order by r.reservationId desc
            """)
    List<Reservation> search(String userText, String roomText, LocalDate checkIn,
                             LocalDate checkOut, ReservationStatus status);

    @Query("""
            select count(d) from ReservationDetail d
            where d.room.roomId = :roomId
              and d.reservation.status <> com.universidad.staytic.model.ReservationStatus.CANCELLED
              and (:reservationId is null or d.reservation.reservationId <> :reservationId)
              and d.scheduledCheckIn < :checkOut
              and d.scheduledCheckOut > :checkIn
            """)
    long countRoomConflicts(Integer roomId, LocalDate checkIn, LocalDate checkOut, Integer reservationId);

    @Query("""
            select distinct r from Reservation r
            join r.details d
            where r.checkInDateTime is not null
              and (:roomText is null or lower(d.room.number) like lower(concat('%', :roomText, '%')))
              and (:fromDate is null or r.checkInDateTime >= :fromDate)
              and (:toDate is null or r.checkInDateTime <= :toDate)
              and (:employeeText is null or lower(r.employeeCheckIn.name) like lower(concat('%', :employeeText, '%'))
                   or lower(r.employeeCheckIn.email) like lower(concat('%', :employeeText, '%')))
            order by r.checkInDateTime desc
            """)
    List<Reservation> searchCheckIns(String roomText, LocalDateTime fromDate,
                                     LocalDateTime toDate, String employeeText);

    @Query("""
            select distinct r from Reservation r
            join r.details d
            where r.checkOutDateTime is not null
              and (:fromDate is null or r.checkOutDateTime >= :fromDate)
              and (:toDate is null or r.checkOutDateTime <= :toDate)
              and (:additionalCharges is null or r.additionalCharges = :additionalCharges)
              and (:penalty is null or r.penalty = :penalty)
              and (:roomText is null or lower(d.room.number) like lower(concat('%', :roomText, '%')))
            order by r.checkOutDateTime desc
            """)
    List<Reservation> searchCheckOuts(LocalDateTime fromDate, LocalDateTime toDate,
                                      Float additionalCharges, Float penalty, String roomText);
}
