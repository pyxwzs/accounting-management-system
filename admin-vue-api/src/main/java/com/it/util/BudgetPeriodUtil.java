package com.it.util;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * 预算周期起止日期（与收支记录 day 字段 yyyy-MM-dd 对齐）
 */
public final class BudgetPeriodUtil {

    private BudgetPeriodUtil() {
    }

    public static String normalizeCycleType(String cycleType) {
        if (cycleType == null || cycleType.trim().isEmpty()) {
            return "none";
        }
        return cycleType.trim().toLowerCase();
    }

    /**
     * @return null 表示不按周期（累计全部关联支出）
     */
    public static LocalDate[] currentPeriodRange(String cycleType) {
        String ct = normalizeCycleType(cycleType);
        if ("none".equals(ct)) {
            return null;
        }
        LocalDate today = LocalDate.now();
        switch (ct) {
            case "month": {
                LocalDate start = today.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate end = today.with(TemporalAdjusters.lastDayOfMonth());
                return new LocalDate[]{start, end};
            }
            case "quarter": {
                int m = today.getMonthValue();
                int startMonth = ((m - 1) / 3) * 3 + 1;
                LocalDate start = LocalDate.of(today.getYear(), startMonth, 1);
                LocalDate end = start.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
                return new LocalDate[]{start, end};
            }
            case "year": {
                LocalDate start = LocalDate.of(today.getYear(), 1, 1);
                LocalDate end = LocalDate.of(today.getYear(), 12, 31);
                return new LocalDate[]{start, end};
            }
            default:
                return null;
        }
    }

    public static LocalDate[] previousPeriodRange(String cycleType) {
        String ct = normalizeCycleType(cycleType);
        if ("none".equals(ct)) {
            return null;
        }
        LocalDate[] cur = currentPeriodRange(ct);
        if (cur == null) {
            return null;
        }
        switch (ct) {
            case "month": {
                LocalDate first = cur[0].minusMonths(1);
                LocalDate start = first.with(TemporalAdjusters.firstDayOfMonth());
                LocalDate end = first.with(TemporalAdjusters.lastDayOfMonth());
                return new LocalDate[]{start, end};
            }
            case "quarter": {
                LocalDate start = cur[0].minusMonths(3);
                LocalDate s = LocalDate.of(start.getYear(), start.getMonthValue(), 1);
                LocalDate e = s.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
                return new LocalDate[]{s, e};
            }
            case "year": {
                int y = cur[0].getYear() - 1;
                return new LocalDate[]{LocalDate.of(y, 1, 1), LocalDate.of(y, 12, 31)};
            }
            default:
                return null;
        }
    }
}
