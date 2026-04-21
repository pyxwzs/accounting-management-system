package com.it.schedule;

import com.it.service.FinanceRssSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时拉取 {@link FinanceRssSyncService} 配置的 RSS，写入财经资讯（与手动「同步」共用逻辑）。
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "finance.rss.scheduled.enabled", havingValue = "true", matchIfMissing = true)
public class FinanceRssScheduler {

    @Resource
    private FinanceRssSyncService financeRssSyncService;

    @Scheduled(cron = "${finance.rss.cron:0 0 */2 * * ?}")
    public void scheduledSync() {
        int n = financeRssSyncService.sync();
        if (n > 0) {
            log.info("RSS 定时同步完成，新增 {} 条资讯", n);
        }
    }
}
