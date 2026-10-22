package com.dtazziboot.tools.service;

import com.dtazziboot.tools.dao.entity.InvokeLogDO;

/**
 * 调用埋点记录服务。
 */
public interface InvokeLogService {

    /**
     * 异步记录一次接口调用。
     *
     * <p>落库失败仅记录 WARN 日志，不阻塞主链路。</p>
     */
    void record(InvokeLogDO log);
}