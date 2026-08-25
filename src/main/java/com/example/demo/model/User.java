package com.example.demo.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    @Column(nullable = false, length = 255)
    private String password;
    @Column(name = "person_type", length = 50)
    private String personType;
    @Column(name = "person_level", length = 50)
    private String personLevel;
    @Column(name = "person_dept", length = 100)
    private String personDept;
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now(ZoneOffset.UTC);
    public User() {}
    public User(String username, String password, String personType, String personLevel, String personDept) {
        this.username = username; this.password = password;
        this.personType = personType; this.personLevel = personLevel; this.personDept = personDept;
    }
    // Getters and setters for all fields
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; } public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
    public String getPersonType() { return personType; } public void setPersonType(String personType) { this.personType = personType; }
    public String getPersonLevel() { return personLevel; } public void setPersonLevel(String personLevel) { this.personLevel = personLevel; }
    public String getPersonDept() { return personDept; } public void setPersonDept(String personDept) { this.personDept = personDept; }
    public LocalDateTime getCreatedAt() { return createdAt; } public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}