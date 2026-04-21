package com.it.service;

import com.it.entity.Plan;
import com.it.pojo.IncomeExpenseSummary;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 存钱进度与基于近30天收支的按期完成预测
 */
@Component
public class PlanForecastHelper {

    private static final int RECENT_DAYS = 30;

    @Resource
    private InfoService infoService;

    public void enrichPlans(List<Plan> plans, String userId) {
        if (plans == null || plans.isEmpty() || userId == null) {
            return;
        }
        LocalDate today = LocalDate.now();
        LocalDate winStart = today.minusDays(RECENT_DAYS - 1);
        IncomeExpenseSummary sum = infoService.sumIncomeExpenseBetween(userId, winStart, today);
        float netTotal = sum.getTotalIncome() - sum.getTotalExpense();
        float netDaily = netTotal / (float) RECENT_DAYS;
        boolean hasData = sum.getTotalIncome() > 0f || sum.getTotalExpense() > 0f;

        for (Plan p : plans) {
            enrichOne(p, today, netDaily, hasData);
        }
    }

    private void enrichOne(Plan p, LocalDate today, float netDaily, boolean hasData) {
        float price = p.getPrice() == null ? 0f : p.getPrice();
        float has = p.getHasPrice() == null ? 0f : p.getHasPrice();
        float remaining = Math.max(0f, price - has);
        p.setProgress_percent(price > 0 ? Math.min(100f, (has / price) * 100f) : 0f);

        LocalDate end = parseDate(p.getEnd_time());
        LocalDate start = parseDate(p.getStart_time());
        if (start != null && end != null) {
            long totalDays = ChronoUnit.DAYS.between(start, end);
            if (totalDays > 0) {
                long passed = ChronoUnit.DAYS.between(start, today);
                float tp = (float) passed / (float) totalDays * 100f;
                p.setTime_progress_percent(Math.min(100f, Math.max(0f, tp)));
            } else {
                p.setTime_progress_percent(0f);
            }
        } else {
            p.setTime_progress_percent(null);
        }

        if (end == null) {
            p.setForecast_message("未设置截止日期，无法预测。");
            p.setForecast_on_track(null);
            p.setRequired_daily_save(null);
            p.setRecent_net_daily(hasData ? netDaily : null);
            return;
        }

        long daysLeft = ChronoUnit.DAYS.between(today, end);

        if (remaining <= 0.01f) {
            p.setForecast_message("目标金额已达成。");
            p.setForecast_on_track(true);
            p.setRequired_daily_save(0f);
            p.setRecent_net_daily(hasData ? netDaily : null);
            return;
        }

        if (daysLeft <= 0) {
            p.setForecast_message("计划已到期，尚未存满目标金额。");
            p.setForecast_on_track(false);
            p.setRequired_daily_save(null);
            p.setRecent_net_daily(hasData ? netDaily : null);
            return;
        }

        float required = remaining / (float) daysLeft;
        p.setRequired_daily_save(required);
        p.setRecent_net_daily(hasData ? netDaily : null);

        if (!hasData) {
            p.setForecast_message(String.format(
                    "近%d天暂无收支记录，无法根据消费习惯预测；要按期存满约需日均存入 %.2f 元。",
                    RECENT_DAYS, required));
            p.setForecast_on_track(null);
            return;
        }

        if (netDaily + 1e-3f >= required) {
            p.setForecast_message(String.format(
                    "按近%d天收支，日均结余约 %.2f 元；距截止还有 %d 天，按期存满约需日均 %.2f 元。当前节奏有望按期完成。",
                    RECENT_DAYS, netDaily, daysLeft, required));
            p.setForecast_on_track(true);
        } else {
            p.setForecast_message(String.format(
                    "按近%d天收支，日均结余约 %.2f 元；按期存满至少需日均 %.2f 元。若延续当前支出水平，可能难以按期完成，建议控制支出或调整目标/期限。",
                    RECENT_DAYS, netDaily, required));
            p.setForecast_on_track(false);
        }
    }

    private static LocalDate parseDate(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        String d = s.length() >= 10 ? s.substring(0, 10) : s.trim();
        try {
            return LocalDate.parse(d);
        } catch (Exception e) {
            return null;
        }
    }
}
