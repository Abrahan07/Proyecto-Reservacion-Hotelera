package com.universidad.staytic.repository;

import com.universidad.staytic.model.Room;
import com.universidad.staytic.model.RoomStatus;
import com.universidad.staytic.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    boolean existsByNumber(String number);
    boolean existsByNumberAndRoomIdNot(String number, Integer roomId);
    long countByStatus(RoomStatus status);

    @Query("""
            select r from Room r
            where (:number is null or lower(r.number) like lower(concat('%', :number, '%')))
              and (:floor is null or r.floor = :floor)
              and (:type is null or r.type = :type)
              and (:price is null or r.pricePerNight = :price)
              and (:status is null or r.status = :status)
            order by r.floor asc, r.number asc
            """)
    List<Room> search(String number, Integer floor, RoomType type, Float price, RoomStatus status);
}
