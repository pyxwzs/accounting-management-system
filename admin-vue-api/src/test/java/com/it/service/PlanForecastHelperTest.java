package com.it.service;

import com.it.entity.Plan;
import com.it.pojo.IncomeExpenseSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlanForecastHelperTest {

    @Mock
    private InfoService infoService;

    @InjectMocks
    private PlanForecastHelper planForecastHelper;

    private String userId;

    @BeforeEach
    void setUp() {
        userId = "u1";
    }

    @Test
    void enrichPlans_empty_noop() {
        planForecastHelper.enrichPlans(null, userId);
        planForecastHelper.enrichPlans(Collections.emptyList(), userId);
    }

    @Test
    void enrichPlans_noIncomeExpense_showsCannotPredictMessage() {
        LocalDate today = LocalDate.now();
        Plan p = plan(today.plusDays(30), 1000f, 0f, today.minusDays(5));

        IncomeExpenseSummary empty = new IncomeExpenseSummary();
        empty.setTotalIncome(0f);
        empty.setTotalExpense(0f);
        when(infoService.sumIncomeExpenseBetween(eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(empty);

        planForecastHelper.enrichPlans(List.of(p), userId);

        assertNotNull(p.getForecast_message());
        assertTrue(p.getForecast_message().contains("暂无收支") || p.getForecast_message().contains("无法根据消费习惯预测"));
        assertNull(p.getForecast_on_track());
    }

    @Test
    void enrichPlans_netDailyAboveRequired_onTrack() {
        LocalDate today = LocalDate.now();
        Plan p = plan(today.plusDays(10), 1000f, 0f, today.minusDays(5));
        // required ≈ 1000/10 = 100；netDaily = 3000/30 = 100，边界用略高结余
        IncomeExpenseSummary sum = new IncomeExpenseSummary();
        sum.setTotalIncome(4000f);
        sum.setTotalExpense(0f);
        when(infoService.sumIncomeExpenseBetween(eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(sum);

        planForecastHelper.enrichPlans(List.of(p), userId);

        assertEquals(Boolean.TRUE, p.getForecast_on_track());
        assertNotNull(p.getRecent_net_daily());
    }

    @Test
    void enrichPlans_goalReached() {
        LocalDate today = LocalDate.now();
        Plan p = plan(today.plusDays(5), 100f, 100f, today.minusDays(1));

        IncomeExpenseSummary sum = new IncomeExpenseSummary();
        sum.setTotalIncome(0f);
        sum.setTotalExpense(0f);
        when(infoService.sumIncomeExpenseBetween(eq(userId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(sum);

        planForecastHelper.enrichPlans(List.of(p), userId);

        assertTrue(p.getForecast_message().contains("达成"));
        assertEquals(Boolean.TRUE, p.getForecast_on_track());
    }

    private static Plan plan(LocalDate end, float price, float has, LocalDate start) {
        Plan p = new Plan();
        p.setPrice(price);
        p.setHasPrice(has);
        p.setEnd_time(end.toString());
        p.setStart_time(start.toString());
        return p;
    }
}
