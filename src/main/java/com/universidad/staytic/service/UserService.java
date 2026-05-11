package com.universidad.staytic.service;

import com.universidad.staytic.model.Role;
import com.universidad.staytic.model.User;
import com.universidad.staytic.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Transactional
    public void register(User usuario) {
        if (repo.existsByEmail(usuario.getEmail()))
            throw new RuntimeException("Email already registered");
        usuario.setPassword(encoder.encode(usuario.getPassword()));
        usuario.setRole(Role.GUEST);
        repo.save(usuario);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> findAll() {
        return repo.findAll();
    }

    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void changeRole(Integer id, Role newRole) {
        User u = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        u.setRole(newRole);
    }

    @PreAuthorize("#usuario.email == authentication.name or hasRole('ADMIN')")
    @Transactional
    public void updateName(User usuario) {
        User existing = repo.findById(usuario.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setName(usuario.getName());
    }
}
