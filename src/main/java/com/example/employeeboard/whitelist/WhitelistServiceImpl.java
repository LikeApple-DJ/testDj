package com.example.employeeboard.whitelist;

import com.example.employeeboard.common.BizException;
import com.example.employeeboard.common.PageResult;
import com.example.employeeboard.whitelist.mapper.WhitelistMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 白名单服务实现。
 */
@Service
public class WhitelistServiceImpl implements WhitelistService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WhitelistServiceImpl.class);
    private static final String DEFAULT_TENANT = "default";

    private final WhitelistMapper whitelistMapper;

    public WhitelistServiceImpl(WhitelistMapper whitelistMapper) {
        this.whitelistMapper = whitelistMapper;
    }

    @Override
    public boolean isAllowed(String empNo) {
        if (!StringUtils.hasText(empNo)) {
            return false;
        }
        return whitelistMapper.selectByEmpNo(DEFAULT_TENANT, empNo) != null;
    }

    @Override
    public void add(String empNo, String userName, String remark, String createdBy) {
        if (!StringUtils.hasText(empNo) || empNo.length() > 32
                || !StringUtils.hasText(userName) || userName.length() > 64) {
            throw new BizException("WL_003", "白名单参数非法");
        }
        WhitelistDO existing = whitelistMapper.selectByEmpNo(DEFAULT_TENANT, empNo);
        if (existing != null) {
            throw new BizException("WL_002", "白名单已存在");
        }
        WhitelistDO record = new WhitelistDO();
        record.setTenantId(DEFAULT_TENANT);
        record.setEmpNo(empNo);
        record.setUserName(userName);
        record.setRemark(remark == null ? "" : remark);
        record.setCreatedBy(createdBy == null ? "" : createdBy);
        // 软删后重新添加：唯一索引冲突时复活原记录
        try {
            whitelistMapper.insert(record);
        } catch (DuplicateKeyException e) {
            LOGGER.warn("whitelist duplicate key, revive soft-deleted record, empNo={}", empNo);
            whitelistMapper.revive(record);
        }
        LOGGER.info("whitelist added, empNo={}, operator={}", empNo, createdBy);
    }

    @Override
    public void remove(String empNo) {
        WhitelistDO existing = whitelistMapper.selectByEmpNo(DEFAULT_TENANT, empNo);
        if (existing == null) {
            throw new BizException("WL_003", "白名单记录不存在");
        }
        if (whitelistMapper.countActive(DEFAULT_TENANT) <= 1) {
            throw new BizException("WL_004", "不允许移除最后一个白名单用户");
        }
        whitelistMapper.softDelete(DEFAULT_TENANT, empNo);
        LOGGER.info("whitelist removed, empNo={}", empNo);
    }

    @Override
    public PageResult<WhitelistDO> pageQuery(int pageNo, int pageSize, String keyword) {
        int safePageNo = Math.max(pageNo, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        long total = whitelistMapper.countPage(DEFAULT_TENANT, keyword);
        List<WhitelistDO> list = whitelistMapper.selectPage(DEFAULT_TENANT, keyword,
                (safePageNo - 1) * safePageSize, safePageSize);
        return new PageResult<>(total, safePageNo, safePageSize, list);
    }
}
