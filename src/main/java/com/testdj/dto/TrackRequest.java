package com.testdj.dto;

import jakarta.validation.constraints.NotBlank;

public class TrackRequest {
    @NotBlank(message = "apiName must not be blank")
    private String apiName;

    @NotBlank(message = "caller must not be blank")
    private String caller;

    @NotBlank(message = "department must not be blank")
    private String department;

    @NotBlank(message = "level must not be blank")
    private String level;

    @NotBlank(message = "type must not be blank")
    private String type;

    public TrackRequest() {}

    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }

    public String getCaller() { return caller; }
    public void setCaller(String caller) { this.caller = caller; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}