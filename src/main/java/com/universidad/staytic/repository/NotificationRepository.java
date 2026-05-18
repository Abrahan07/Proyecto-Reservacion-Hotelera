package com.universidad.staytic.repository;

import com.universidad.staytic.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserEmailOrderBySentAtDesc(String email);
    List<Notification> findAllByOrderBySentAtDesc();
}
