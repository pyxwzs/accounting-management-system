package com.it.service;

import com.it.entity.Budget;
import com.it.util.BudgetPeriodUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;

/**
 * 预算占用率、预警文案、下期建议
 */
@Component
public class BudgetAnalysisHelper {

    @Resource
    private InfoService infoService;

    public void enrich(Budget b) {
        if (b == null) {
            return;
        }
        String ct = BudgetPeriodUtil.normalizeCycleType(b.getCycle_type());
        b.setCycle_type(ct);

        float use = infoService.sumExpenseForBudgetByCycle(b.getId(), ct);
        b.setUse_price(use);

        float price = b.getPrice() != null ? b.getPrice() : 0f;
        b.setResidue_price(price - use);

        if (price <= 0) {
            b.setUsage_ratio(0f);
            b.setAlert_message(null);
            b.setNext_period_suggestion(null);
            b.setPrevious_period_overspend(null);
            return;
        }

        float ratio = use / price;
        b.setUsage_ratio(ratio);

        float warn = b.getWarn_ratio() != null ? b.getWarn_ratio() : 0.85f;
        if (warn <= 0 || warn > 1) {
            warn = 0.85f;
        }

        LocalDate[] prevRange = BudgetPeriodUtil.previousPeriodRange(ct);
        boolean prevOver = false;
        if (prevRange != null && price > 0) {
            float prevUse = infoService.sumExpenseForBudgetBetween(b.getId(), prevRange[0], prevRange[1]);
            prevOver = prevUse > price;
            b.setPrevious_period_overspend(prevOver);
        }

        b.setAlert_message(null);
        b.setNext_period_suggestion(null);

        if (use >= price * warn && use < price) {
            b.setAlert_message(String.format("本周期已使用约 %.0f%% 预算，即将超标，请控制支出。", ratio * 100));
        } else if (use >= price) {
            b.setAlert_message("本周期预算已超支。");
            b.setNext_period_suggestion(buildNextPlan(price, use, prevOver, ct));
        } else if (Boolean.TRUE.equals(prevOver)) {
            float dailyHint = dailyCapHint(price, ct);
            b.setNext_period_suggestion(String.format(
                    "上周期已超支，本周期建议将日均消费控制在约 %.2f 元以内，或适当提高周期预算。", dailyHint));
        }
    }

    private static float dailyCapHint(float budget, String cycle) {
        if (budget <= 0) {
            return 0f;
        }
        switch (cycle) {
            case "quarter":
                return budget / 90f;
            case "year":
                return budget / 365f;
            case "month":
            default:
                return budget / 30f;
        }
    }

    private static String buildNextPlan(float budget, float used, boolean prevOver, String cycle) {
        float daily = dailyCapHint(budget, cycle);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("下期建议：若维持本期消费水平，可将周期预算调整为不低于 %.2f 元；", used));
        sb.append(String.format("若维持预算 %.2f 元，建议将周期内日均消费控制在 %.2f 元以内。", budget, daily));
        if (prevOver) {
            sb.append(" 已连续周期超支，建议审视固定支出并预留缓冲。");
        }
        return sb.toString();
    }
}
