package com.universidad.staytic.repository;

import com.universidad.staytic.model.Payment;
import com.universidad.staytic.model.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    void deleteByReservationReservationId(Integer reservationId);

    @Query("""
            select p from Payment p
            where (:reservationText is null
                   or str(p.reservation.reservationId) like concat('%', :reservationText, '%')
                   or lower(p.reservation.user.name) like lower(concat('%', :reservationText, '%'))
                   or lower(p.reservation.user.email) like lower(concat('%', :reservationText, '%')))
              and (:amount is null or p.amount = :amount)
              and (:method is null or p.paymentMethod = :method)
              and (:paymentDate is null or p.paymentDate = :paymentDate)
            order by p.paymentDate desc, p.paymentId desc
            """)
    List<Payment> search(String reservationText, Float amount, PaymentMethod method, LocalDate paymentDate);
}
