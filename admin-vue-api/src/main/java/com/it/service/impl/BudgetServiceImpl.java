package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Budget;
import com.it.mapper.BudgetMapper;
import com.it.service.BudgetService;
import com.it.util.AuthUserUtils;
import com.it.util.CommonUtils;
import com.it.util.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户service实现类
 *
 * @author
 */
@Service
public class BudgetServiceImpl implements BudgetService {

    @Resource
    private BudgetMapper BudgetMapper;
    @Resource
    private AuthUserUtils authUserUtils;

    @Override
    public IPage<Budget> selectPage(Budget entity, int page, int limit) {
        QueryWrapper<Budget> searchInfo = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getName())) {
            searchInfo.like("name", entity.getName());
        }
        if (CommonUtils.stringIsNotBlack(entity.getUser_id())) {
            searchInfo.like("user_id", entity.getUser_id());
        }
        if (CommonUtils.stringIsNotBlack(entity.getCreate_time())) {
            searchInfo.between("create_time", entity.getCreate_time().split(" - ")[0], entity.getCreate_time().split(" - ")[1]);
        }
        searchInfo.orderBy(true, false, "create_time");
        IPage<Budget> pageInfo = new Page<>(page, limit);
        IPage<Budget> selectPage = BudgetMapper.selectPage(pageInfo, searchInfo);
        return selectPage;
    }

    @Override
    public Budget selectByPrimaryKey(String id) {
        Budget Budget = BudgetMapper.selectById(id);
        return Budget;
    }

    @Override
    public boolean insert(Budget entity) {
        entity.setCreate_time(DateUtil.getNowDateSS());
        entity.setUser_id(authUserUtils.getLoginUserId());
        if (entity.getWarn_ratio() == null) {
            entity.setWarn_ratio(0.85f);
        }
        if (entity.getCycle_type() == null || entity.getCycle_type().trim().isEmpty()) {
            entity.setCycle_type("month");
        }
        Integer result = BudgetMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Budget entity) {
        Integer result = BudgetMapper.updateById(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        Integer result = 0;
        for (String id : ids) {
            result = BudgetMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Budget> selectList(Budget entity) {
        QueryWrapper<Budget> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getName())) {
            wrapper.like("name", entity.getName());
        }
        if (CommonUtils.stringIsNotBlack(entity.getUser_id())) {
            wrapper.like("user_id", entity.getUser_id());
        }
        wrapper.orderBy(true, false, "create_time");
        List<Budget> BudgetList = BudgetMapper.selectList(wrapper);
        return BudgetList;
    }
}