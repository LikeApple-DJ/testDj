package com.example.employeeboard.budget;

import com.example.employeeboard.common.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 成本预算管理端接口（W10-W11）。
 */
@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping("/save")
    public Result<Boolean> save(@Valid @RequestBody BudgetSaveReq req) {
        return Result.ok(budgetService.saveBudget(req));
    }

    @GetMapping("/queryByEmp")
    public Result<List<BudgetDTO>> queryByEmp(@RequestParam String empNo) {
        return Result.ok(budgetService.queryByEmpNo(empNo));
    }
}
