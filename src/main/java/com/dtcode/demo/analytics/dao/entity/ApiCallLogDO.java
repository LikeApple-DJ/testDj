package com.dtcode.demo.analytics.dao.entity;

import java.util.Date;

/**
 * 接口调用日志数据对象
 *
 * @author DTCoder
 */
public class ApiCallLogDO {

    private Long id;
    private String apiName;
    private String callerId;
    private String callerName;
    private String callerType;
    private String callerLevel;
    private String callerDept;
    private String requestParams;
    private String responseStatus;
    private Integer callDurationMs;
    private Date gmtCreate;
    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getCallerType() {
        return callerType;
    }

    public void setCallerType(String callerType) {
        this.callerType = callerType;
    }

    public String getCallerLevel() {
        return callerLevel;
    }

    public void setCallerLevel(String callerLevel) {
        this.callerLevel = callerLevel;
    }

    public String getCallerDept() {
        return callerDept;
    }

    public void setCallerDept(String callerDept) {
        this.callerDept = callerDept;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Integer getCallDurationMs() {
        return callDurationMs;
    }

    public void setCallDurationMs(Integer callDurationMs) {
        this.callDurationMs = callDurationMs;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public String toString() {
        return "ApiCallLogDO{id=" + id + ", apiName='" + apiName + "', callerId='" + callerId
                + "', gmtCreate=" + gmtCreate + "}";
    }
}
