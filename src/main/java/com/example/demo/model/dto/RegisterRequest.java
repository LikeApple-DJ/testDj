package com.example.demo.model.dto;
public class RegisterRequest {
    private String username; private String password;
    private String personType; private String personLevel; private String personDept;
    public String getUsername() { return username; } public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
    public String getPersonType() { return personType; } public void setPersonType(String personType) { this.personType = personType; }
    public String getPersonLevel() { return personLevel; } public void setPersonLevel(String personLevel) { this.personLevel = personLevel; }
    public String getPersonDept() { return personDept; } public void setPersonDept(String personDept) { this.personDept = personDept; }
}