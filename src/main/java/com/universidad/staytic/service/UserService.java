package com.universidad.staytic.service;

import com.universidad.staytic.dto.UserForm;
import com.universidad.staytic.dto.ProfileForm;
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
    public void register(User user) {
        System.out.println(">>> Registering: " + user.getEmail());
        if (repo.existsByEmail(user.getEmail()))
            throw new RuntimeException("Email already registered");
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(Role.GUEST);
        repo.save(user);
        System.out.println(">>> Saved successfully!");
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> findAll() {
        return repo.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> search(String name, String email, Role role) {
        return repo.search(blankToNull(name), blankToNull(email), role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<User> findById(Integer id) {
        return repo.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    public Optional<User> findByEmail(String email) {
        return repo.findByEmail(email);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void createFromAdmin(UserForm form) {
        if (repo.existsByEmail(form.getEmail())) {
            throw new RuntimeException("El correo ya esta registrado");
        }
        if (form.getPassword() == null || form.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña es obligatoria");
        }
        User user = new User();
        applyForm(user, form);
        user.setPassword(encoder.encode(form.getPassword()));
        repo.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void updateFromAdmin(Integer id, UserForm form) {
        User user = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (repo.existsByEmailAndUserIdNot(form.getEmail(), id)) {
            throw new RuntimeException("El correo ya esta registrado por otro usuario");
        }
        applyForm(user, form);
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPassword(encoder.encode(form.getPassword()));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void delete(Integer id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        repo.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void changeRole(Integer id, Role newRole) {
        User u = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        u.setRole(newRole);
    }

    @PreAuthorize("#user.email == authentication.name or hasRole('ADMIN')")
    @Transactional
    public void updateName(User user) {
        User existing = repo.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        existing.setName(user.getName());
    }

    public UserForm toForm(User user) {
        UserForm form = new UserForm();
        form.setUserId(user.getUserId());
        form.setName(user.getName());
        form.setEmail(user.getEmail());
        form.setPhone(user.getPhone());
        form.setRole(user.getRole());
        form.setActive(user.isActive());
        return form;
    }

    @PreAuthorize("#currentEmail == authentication.name")
    @Transactional
    public void updateProfile(String currentEmail, ProfileForm form) {
        User user = repo.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (repo.existsByEmailAndUserIdNot(form.getEmail(), user.getUserId())) {
            throw new RuntimeException("El correo ya esta registrado por otro usuario");
        }
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPhone(form.getPhone());
        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.setPassword(encoder.encode(form.getPassword()));
        }
    }

    public ProfileForm toProfileForm(User user) {
        ProfileForm form = new ProfileForm();
        form.setName(user.getName());
        form.setEmail(user.getEmail());
        form.setPhone(user.getPhone());
        return form;
    }

    private void applyForm(User user, UserForm form) {
        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setPhone(form.getPhone());
        user.setRole(form.getRole());
        user.setActive(form.isActive());
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
