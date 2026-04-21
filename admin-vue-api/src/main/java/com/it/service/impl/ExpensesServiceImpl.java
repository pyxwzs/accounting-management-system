package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Expenses;
import com.it.mapper.ExpensesMapper;
import com.it.service.ExpensesService;
import com.it.util.AuthUserUtils;
import com.it.util.CommonUtils;
import com.it.util.EmojiFilter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户service实现类
 *
 * @author
 */
@Service
public class ExpensesServiceImpl implements ExpensesService {

    @Resource
    private ExpensesMapper ExpensesMapper;

    @Resource
    private AuthUserUtils authUserUtils;

    @Override
    public IPage<Expenses> selectPage(Expenses entity, int page, int limit) {
        QueryWrapper<Expenses> searchInfo = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getUser_id())) {
            searchInfo.like("user_id", entity.getUser_id());
        }
        if (CommonUtils.stringIsNotBlack(entity.getContent())) {
            searchInfo.like("content", entity.getContent());
        }
        if (CommonUtils.stringIsNotBlack(entity.getState())) {
            searchInfo.like("state", entity.getState());
        }
        if (CommonUtils.stringIsNotBlack(entity.getExpenses_time())) {
            searchInfo.between("expenses_time", entity.getExpenses_time().split(" - ")[0], entity.getExpenses_time().split(" - ")[1]);
        }
        searchInfo.orderBy(true, false, "expenses_time");
        IPage<Expenses> pageInfo = new Page<>(page, limit);
        IPage<Expenses> selectPage = ExpensesMapper.selectPage(pageInfo, searchInfo);
        return selectPage;
    }

    @Override
    public Expenses selectByPrimaryKey(String id) {
        Expenses Expenses = ExpensesMapper.selectById(id);
        Expenses.setContent(EmojiFilter.emojiRecovery2(Expenses.getContent()));
        return Expenses;
    }

    @Override
    public boolean insert(Expenses entity) {

        entity.setUser_id(authUserUtils.getLoginUserId());
        Integer result = ExpensesMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Expenses entity) {
        entity.setContent(EmojiFilter.emojiConvert1(entity.getContent()));
        Integer result = ExpensesMapper.updateById(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        Integer result = 0;
        for (String id : ids) {
            result = ExpensesMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Expenses> selectList(Expenses entity) {
        QueryWrapper<Expenses> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getUser_id())) {
            wrapper.like("user_id", entity.getUser_id());
        }
        wrapper.orderBy(true, false, "expenses_time");
        List<Expenses> ExpensesList = ExpensesMapper.selectList(wrapper);
        return ExpensesList;
    }
}