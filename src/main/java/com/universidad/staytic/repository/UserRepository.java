package com.universidad.staytic.repository;

import com.universidad.staytic.model.User;
import com.universidad.staytic.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByEmailAndUserIdNot(String email, Integer userId);

    @Query("""
            select u from User u
            where (:name is null or lower(u.name) like lower(concat('%', :name, '%')))
              and (:email is null or lower(u.email) like lower(concat('%', :email, '%')))
              and (:role is null or u.role = :role)
            order by u.userId desc
            """)
    List<User> search(String name, String email, Role role);
}
