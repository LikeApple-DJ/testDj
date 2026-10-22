package com.example.employeeboard.whitelist;

import com.example.employeeboard.common.PageResult;
import com.example.employeeboard.common.Result;
import com.example.employeeboard.common.UserContext;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 白名单管理端接口。
 */
@RestController
@RequestMapping("/api/whitelist")
@Validated
public class WhitelistController {

    private final WhitelistService whitelistService;

    public WhitelistController(WhitelistService whitelistService) {
        this.whitelistService = whitelistService;
    }

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Map<String, String> req) {
        String empNo = req.get("empNo");
        String userName = req.get("userName");
        if (empNo == null || empNo.isBlank() || userName == null || userName.isBlank()) {
            throw new com.example.employeeboard.common.BizException("WL_003", "empNo/userName 必填");
        }
        whitelistService.add(empNo, userName, req.get("remark"), UserContext.getCurrentEmpNo());
        return Result.ok(Boolean.TRUE);
    }

    @PostMapping("/remove")
    public Result<Boolean> remove(@RequestBody Map<String, @NotBlank String> req) {
        whitelistService.remove(req.get("empNo"));
        return Result.ok(Boolean.TRUE);
    }

    @PostMapping("/pageQuery")
    public Result<PageResult<WhitelistDO>> pageQuery(@RequestBody Map<String, Object> req) {
        int pageNo = req.get("pageNo") == null ? 1 : Integer.parseInt(req.get("pageNo").toString());
        int pageSize = req.get("pageSize") == null ? 20 : Integer.parseInt(req.get("pageSize").toString());
        String keyword = req.get("keyword") == null ? null : req.get("keyword").toString();
        return Result.ok(whitelistService.pageQuery(pageNo, pageSize, keyword));
    }
}
