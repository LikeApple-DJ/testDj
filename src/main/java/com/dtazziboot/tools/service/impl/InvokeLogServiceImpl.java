package com.dtazziboot.tools.service.impl;

import com.dtazziboot.tools.dao.entity.InvokeLogDO;
import com.dtazziboot.tools.dao.mapper.InvokeLogMapper;
import com.dtazziboot.tools.service.InvokeLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 调用埋点记录服务实现：异步落库，失败不影响主链路。
 */
@Service
public class InvokeLogServiceImpl implements InvokeLogService {

    private static final Logger logger = LoggerFactory.getLogger(InvokeLogServiceImpl.class);

    private final InvokeLogMapper invokeLogMapper;

    public InvokeLogServiceImpl(InvokeLogMapper invokeLogMapper) {
        this.invokeLogMapper = invokeLogMapper;
    }

    @Async("invokeLogExecutor")
    @Override
    public void record(InvokeLogDO log) {
        try {
            invokeLogMapper.insert(log);
        } catch (Exception e) {
            logger.warn("埋点落库失败, apiCode: {}, invokerId: {}", log.getApiCode(), log.getInvokerId(), e);
        }
    }
}