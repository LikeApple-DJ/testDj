package com.testdj.dto;

import java.util.Map;

public class ExportRequest {
    private String tab;
    private Map<String, Object> resultData;

    public ExportRequest() {}

    public ExportRequest(String tab, Map<String, Object> resultData) {
        this.tab = tab;
        this.resultData = resultData;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public Map<String, Object> getResultData() {
        return resultData;
    }

    public void setResultData(Map<String, Object> resultData) {
        this.resultData = resultData;
    }
}