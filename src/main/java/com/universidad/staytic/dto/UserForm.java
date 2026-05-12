package com.universidad.staytic.dto;

import com.universidad.staytic.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserForm {

    private Integer userId;

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ ]{3,100}$",
            message = "El nombre solo puede contener letras y espacios")
    private String name;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Debe ser un correo valido")
    private String email;

    @Size(max = 100, message = "La contraseña no puede superar 100 caracteres")
    private String password;

    @Pattern(regexp = "^$|^[0-9+()\\- ]{7,20}$",
            message = "El telefono solo puede contener numeros, espacios y + - ()")
    private String phone;

    @NotNull(message = "El rol es obligatorio")
    private Role role;

    private boolean active = true;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
