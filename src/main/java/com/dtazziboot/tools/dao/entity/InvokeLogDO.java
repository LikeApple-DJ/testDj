package com.dtazziboot.tools.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 接口调用埋点流水表数据对象。
 */
@TableName("api_invoke_log")
public class InvokeLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 接口编码。 */
    private String apiCode;

    /** 接口名称。 */
    private String apiName;

    /** 调用人 ID。 */
    private String invokerId;

    /** 调用人姓名。 */
    private String invokerName;

    /** 人员类型。 */
    private String personType;

    /** 人员层级。 */
    private String personLevel;

    /** 人员部门。 */
    private String personDept;

    /** 调用时间。 */
    private LocalDateTime invokeTime;

    /** 调用耗时(ms)。 */
    private Integer durationMs;

    /** 结果码。 */
    private String resultCode;

    /** 调用来源 IP。 */
    private String ip;

    /** 创建时间。 */
    private LocalDateTime gmtCreate;

    /** 修改时间。 */
    private LocalDateTime gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApiCode() {
        return apiCode;
    }

    public void setApiCode(String apiCode) {
        this.apiCode = apiCode;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getInvokerId() {
        return invokerId;
    }

    public void setInvokerId(String invokerId) {
        this.invokerId = invokerId;
    }

    public String getInvokerName() {
        return invokerName;
    }

    public void setInvokerName(String invokerName) {
        this.invokerName = invokerName;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getPersonLevel() {
        return personLevel;
    }

    public void setPersonLevel(String personLevel) {
        this.personLevel = personLevel;
    }

    public String getPersonDept() {
        return personDept;
    }

    public void setPersonDept(String personDept) {
        this.personDept = personDept;
    }

    public LocalDateTime getInvokeTime() {
        return invokeTime;
    }

    public void setInvokeTime(LocalDateTime invokeTime) {
        this.invokeTime = invokeTime;
    }

    public Integer getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Integer durationMs) {
        this.durationMs = durationMs;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public LocalDateTime getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(LocalDateTime gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public String toString() {
        return "InvokeLogDO{"
                + "id=" + id
                + ", apiCode='" + apiCode + '\''
                + ", apiName='" + apiName + '\''
                + ", invokerId='" + invokerId + '\''
                + ", invokerName='" + invokerName + '\''
                + ", personType='" + personType + '\''
                + ", personLevel='" + personLevel + '\''
                + ", personDept='" + personDept + '\''
                + ", invokeTime=" + invokeTime
                + ", durationMs=" + durationMs
                + ", resultCode='" + resultCode + '\''
                + ", ip='" + ip + '\''
                + '}';
    }
}