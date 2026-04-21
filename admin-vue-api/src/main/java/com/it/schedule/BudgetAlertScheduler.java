package com.it.schedule;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.it.entity.Budget;
import com.it.entity.User;
import com.it.mapper.BudgetMapper;
import com.it.mapper.UserMapper;
import com.it.service.BudgetAnalysisHelper;
import com.it.util.BudgetPeriodUtil;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;

/**
 * 每日定时检查周期预算，达到预警比例时发送邮件（同一预算同一天不重复发送）
 */
@Component
public class BudgetAlertScheduler {

    private static final String FROM = "1156326165@qq.com";

    @Resource
    private BudgetMapper budgetMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private BudgetAnalysisHelper budgetAnalysisHelper;
    @Resource
    private JavaMailSender mailSender;

    @Scheduled(cron = "0 0 9 * * ?")
    public void sendBudgetWarnings() {
        String today = LocalDate.now().toString();
        for (Budget b : budgetMapper.selectList(new QueryWrapper<Budget>().isNotNull("user_id"))) {
            String ct = BudgetPeriodUtil.normalizeCycleType(b.getCycle_type());
            if ("none".equals(ct)) {
                continue;
            }
            budgetAnalysisHelper.enrich(b);
            float price = b.getPrice() != null ? b.getPrice() : 0f;
            if (price <= 0) {
                continue;
            }
            float ratio = b.getUsage_ratio() != null ? b.getUsage_ratio() : 0f;
            float warn = b.getWarn_ratio() != null ? b.getWarn_ratio() : 0.85f;
            if (warn <= 0 || warn > 1) {
                warn = 0.85f;
            }
            if (ratio < warn) {
                continue;
            }
            if (today.equals(b.getLast_email_date())) {
                continue;
            }
            User user = userMapper.selectById(b.getUser_id());
            if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
                continue;
            }
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setFrom(FROM);
                message.setTo(user.getEmail());
                message.setSubject("【个人记账】预算预警：" + (b.getName() == null ? "" : b.getName()));
                StringBuilder body = new StringBuilder();
                body.append("您好，\n\n预算「").append(b.getName() != null ? b.getName() : "").append("」本周期已使用 ");
                body.append(String.format("%.1f%%（已用 %.2f 元 / 预算 %.2f 元）。\n\n",
                        ratio * 100,
                        b.getUse_price() != null ? b.getUse_price() : 0f,
                        price));
                body.append(b.getAlert_message() != null ? b.getAlert_message() : "请关注支出情况。");
                if (b.getNext_period_suggestion() != null) {
                    body.append("\n\n下期建议：\n").append(b.getNext_period_suggestion());
                }
                body.append("\n\n——个人记账系统");
                message.setText(body.toString());
                mailSender.send(mimeMessage);
                budgetMapper.update(null, new UpdateWrapper<Budget>()
                        .eq("id", b.getId())
                        .set("last_email_date", today));
            } catch (Exception ignored) {
                // 单封失败不影响其余预算
            }
        }
    }
}
