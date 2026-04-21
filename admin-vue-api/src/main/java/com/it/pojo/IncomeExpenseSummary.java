package com.it.pojo;

import lombok.Data;

/**
 * 区间内收支汇总（用于存钱计划预测）
 */
@Data
public class IncomeExpenseSummary {
    private float totalIncome;
    private float totalExpense;
    /** 窗口天数，用于计算日均 */
    private int windowDays;
}
