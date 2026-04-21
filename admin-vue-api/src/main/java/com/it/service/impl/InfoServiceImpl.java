package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Budget;
import com.it.entity.Info;
import com.it.mapper.BudgetMapper;
import com.it.mapper.InfoMapper;
import com.it.service.InfoService;
import com.it.util.AuthUserUtils;
import com.it.util.CommonUtils;
import com.it.util.BudgetPeriodUtil;
import com.it.util.DateUtil;
import org.springframework.stereotype.Service;

import com.it.pojo.IncomeExpenseSummary;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * 用户service实现类
 *
 * @author
 */
@Service
public class InfoServiceImpl implements InfoService {

    @Resource
    private InfoMapper InfoMapper;
    @Resource
    private AuthUserUtils authUserUtils;
    @Resource
    private BudgetMapper budgetMapper;

    @Override
    public IPage<Info> selectPage(Info entity, int page, int limit) {
        QueryWrapper<Info> searchInfo = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getName())) {
            searchInfo.like("name", entity.getName());
        }
        if (CommonUtils.stringIsNotBlack(entity.getType())) {
            searchInfo.like("type", entity.getType());
        }
        if (CommonUtils.stringIsNotBlack(entity.getUser_id())) {
            searchInfo.like("user_id", entity.getUser_id());
        }
        if (CommonUtils.stringIsNotBlack(entity.getDay())) {
            searchInfo.between("day", entity.getDay().split(" - ")[0], entity.getDay().split(" - ")[1]);
        }
        if (CommonUtils.stringIsNotBlack(entity.getCreate_time())) {
            searchInfo.between("create_time", entity.getCreate_time().split(" - ")[0], entity.getCreate_time().split(" - ")[1]);
        }
        searchInfo.orderBy(true, false, "create_time");
        IPage<Info> pageInfo = new Page<>(page, limit);
        IPage<Info> selectPage = InfoMapper.selectPage(pageInfo, searchInfo);
        for (Info record : selectPage.getRecords()) {
            Budget budget = budgetMapper.selectById(record.getBudget_id());
            record.setBudget_name(budget == null ? " - " : budget.getName());
        }
        return selectPage;
    }

    @Override
    public Info selectByPrimaryKey(String id) {
        Info Info = InfoMapper.selectById(id);
        return Info;
    }

    @Override
    public boolean insert(Info entity) {
        entity.setCreate_time(DateUtil.getNowDateSS());
        entity.setUser_id(authUserUtils.getLoginUserId());
        Integer result = InfoMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Info entity) {
        Integer result = InfoMapper.updateById(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        Integer result = 0;
        for (String id : ids) {
            result = InfoMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Info> selectList(Info entity) {
        QueryWrapper<Info> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getName())) {
            wrapper.like("name", entity.getName());
        }
        if (CommonUtils.stringIsNotBlack(entity.getType())) {
            wrapper.like("type", entity.getType());
        }
        if (CommonUtils.stringIsNotBlack(entity.getUser_id())) {
            wrapper.like("user_id", entity.getUser_id());
        }
        if (CommonUtils.stringIsNotBlack(entity.getDay())) {
            wrapper.between("day", entity.getDay().split(" - ")[0], entity.getDay().split(" - ")[1]);
        }
        if (CommonUtils.stringIsNotBlack(entity.getCreate_time())) {
            wrapper.between("create_time", entity.getCreate_time().split(" - ")[0], entity.getCreate_time().split(" - ")[1]);
        }
        wrapper.orderBy(true, true, "day");
        List<Info> InfoList = InfoMapper.selectList(wrapper);
        return InfoList;
    }

    @Override
    public Float selectList(String budget_id) {
        QueryWrapper<Info> wrapper = new QueryWrapper<>();
        wrapper.eq("budget_id", budget_id);
        List<Info> InfoList = InfoMapper.selectList(wrapper);
        float price = 0;
        for (Info info : InfoList) {
            if (info.getPrice() != null) {
                price = price + info.getPrice();
            }
        }
        return price;
    }

    @Override
    public Float sumExpenseForBudgetBetween(String budgetId, LocalDate start, LocalDate end) {
        if (budgetId == null || start == null || end == null) {
            return 0f;
        }
        QueryWrapper<Info> wrapper = new QueryWrapper<>();
        wrapper.eq("budget_id", budgetId);
        wrapper.ge("day", start.toString());
        wrapper.le("day", end.toString());
        List<Info> list = InfoMapper.selectList(wrapper);
        float sum = 0f;
        for (Info info : list) {
            if (info.getPrice() != null) {
                sum += info.getPrice();
            }
        }
        return sum;
    }

    @Override
    public Float sumExpenseForBudgetByCycle(String budgetId, String cycleType) {
        LocalDate[] range = BudgetPeriodUtil.currentPeriodRange(cycleType);
        if (range == null) {
            return selectList(budgetId);
        }
        return sumExpenseForBudgetBetween(budgetId, range[0], range[1]);
    }

    @Override
    public IncomeExpenseSummary sumIncomeExpenseBetween(String userId, LocalDate start, LocalDate end) {
        IncomeExpenseSummary s = new IncomeExpenseSummary();
        if (start != null && end != null) {
            s.setWindowDays((int) ChronoUnit.DAYS.between(start, end) + 1);
        }
        if (userId == null || start == null || end == null) {
            return s;
        }
        QueryWrapper<Info> w = new QueryWrapper<>();
        w.eq("user_id", userId);
        w.ge("day", start.toString());
        w.le("day", end.toString());
        List<Info> list = InfoMapper.selectList(w);
        float inc = 0f;
        float exp = 0f;
        for (Info info : list) {
            if (info.getPrice() == null) {
                continue;
            }
            if ("收入".equals(info.getType())) {
                inc += info.getPrice();
            } else if ("支出".equals(info.getType())) {
                exp += info.getPrice();
            }
        }
        s.setTotalIncome(inc);
        s.setTotalExpense(exp);
        return s;
    }
}