package com.example.employeeboard.whitelist;

import com.example.employeeboard.common.PageResult;

/**
 * 白名单服务。
 */
public interface WhitelistService {

    /**
     * 校验工号是否在白名单内。
     *
     * @param empNo 用户工号
     * @return true=允许访问管理端
     */
    boolean isAllowed(String empNo);

    /**
     * 新增白名单。
     *
     * @param empNo     被授权人工号
     * @param userName  姓名
     * @param remark    备注
     * @param createdBy 操作人工号
     */
    void add(String empNo, String userName, String remark, String createdBy);

    /**
     * 移除白名单（软删），不允许移除最后一条有效记录。
     *
     * @param empNo 被移除人工号
     */
    void remove(String empNo);

    /**
     * 白名单分页查询。
     */
    PageResult<WhitelistDO> pageQuery(int pageNo, int pageSize, String keyword);
}
