package com.testdj.dto;

public class ExportRequest {
    private String tab;

    public ExportRequest() {}

    public ExportRequest(String tab) {
        this.tab = tab;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }
}