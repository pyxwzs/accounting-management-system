package com.it.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.entity.Info;
import com.it.pojo.IncomeExpenseSummary;

import java.time.LocalDate;
import java.util.List;

public interface InfoService {


    /**
     * 分页查询
     *
     * @param entity
     * @param page
     * @param limit
     * @return
     */
    IPage<Info> selectPage(Info entity, int page, int limit);

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    Info selectByPrimaryKey(String id);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(Info entity);

    /**
     * 修改
     *
     * @param
     * @return
     */
    boolean updateByPrimaryKey(Info entity);

    /**
     * 删除,单个删除批量删除通用
     *
     * @param ids
     * @return
     */
    boolean deleteByPrimaryKey(List<String> ids);

    /**
     * 列表查询
     *
     * @param entity
     * @return
     */
    List<Info> selectList(Info entity);

    /**
     * 某预算下全部关联支出累计（不按周期）
     */
    Float selectList(String budget_id);

    /**
     * 某预算在日期区间内的支出合计（day 字段 yyyy-MM-dd）
     */
    Float sumExpenseForBudgetBetween(String budgetId, LocalDate start, LocalDate end);

    /**
     * 按周期类型汇总当前周期内支出；cycleType 为 none 时与 selectList(budgetId) 相同
     */
    Float sumExpenseForBudgetByCycle(String budgetId, String cycleType);

    /**
     * 按收支记录 day 字段，汇总区间内收入、支出（type 为「收入」「支出」）
     */
    IncomeExpenseSummary sumIncomeExpenseBetween(String userId, LocalDate start, LocalDate end);
}
