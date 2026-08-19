package com.orgarch.employee;

import java.util.List;

public class EmployeePageVo {
    private int page;
    private int size;
    private long total;
    private List<EmployeeVo> list;

    public EmployeePageVo(int page, int size, long total, List<EmployeeVo> list) {
        this.page = page;
        this.size = size;
        this.total = total;
        this.list = list;
    }

    public int getPage() { return page; }
    public int getSize() { return size; }
    public long getTotal() { return total; }
    public List<EmployeeVo> getList() { return list; }
}
