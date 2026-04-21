package com.it.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BudgetPeriodUtilTest {

    @Test
    void normalizeCycleType_blank_defaultsToNone() {
        assertEquals("none", BudgetPeriodUtil.normalizeCycleType(null));
        assertEquals("none", BudgetPeriodUtil.normalizeCycleType("  "));
    }

    @Test
    void normalizeCycleType_trimsAndLowercases() {
        assertEquals("month", BudgetPeriodUtil.normalizeCycleType(" MONTH "));
    }

    @Test
    void currentPeriodRange_none_returnsNull() {
        assertNull(BudgetPeriodUtil.currentPeriodRange("none"));
        assertNull(BudgetPeriodUtil.currentPeriodRange(null));
    }

    @Test
    void currentPeriodRange_month_containsToday() {
        LocalDate today = LocalDate.now();
        LocalDate[] range = BudgetPeriodUtil.currentPeriodRange("month");
        assertNotNull(range);
        assertFalse(today.isBefore(range[0]));
        assertFalse(today.isAfter(range[1]));
        assertEquals(1, range[0].getDayOfMonth());
    }

    @Test
    void previousPeriodRange_month_beforeCurrent() {
        LocalDate[] cur = BudgetPeriodUtil.currentPeriodRange("month");
        LocalDate[] prev = BudgetPeriodUtil.previousPeriodRange("month");
        assertNotNull(cur);
        assertNotNull(prev);
        assertTrue(prev[1].isBefore(cur[0]) || prev[1].equals(cur[0].minusDays(1)));
    }
}
