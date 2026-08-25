package com.example.demo.model.dto;
public class AuthResponse {
    private Long id; private String username; private String token;
    private String personType; private String personLevel; private String personDept;
    public AuthResponse() {}
    public AuthResponse(Long id, String username, String token) {
        this.id = id; this.username = username; this.token = token;
    }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; } public void setUsername(String username) { this.username = username; }
    public String getToken() { return token; } public void setToken(String token) { this.token = token; }
    public String getPersonType() { return personType; } public void setPersonType(String personType) { this.personType = personType; }
    public String getPersonLevel() { return personLevel; } public void setPersonLevel(String personLevel) { this.personLevel = personLevel; }
    public String getPersonDept() { return personDept; } public void setPersonDept(String personDept) { this.personDept = personDept; }
}