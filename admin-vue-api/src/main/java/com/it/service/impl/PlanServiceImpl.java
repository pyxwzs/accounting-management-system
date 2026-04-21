package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Plan;
import com.it.mapper.PlanMapper;
import com.it.service.PlanService;
import com.it.util.AuthUserUtils;
import com.it.util.CommonUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户service实现类
 *
 * @author
 */
@Service
public class PlanServiceImpl implements PlanService {

    @Resource
    private PlanMapper PlanMapper;

    @Resource
    private AuthUserUtils authUserUtils;

    @Override
    public IPage<Plan> selectPage(Plan entity, int page, int limit) {
        QueryWrapper<Plan> searchInfo = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getUser_id())) {
            searchInfo.eq("user_id", entity.getUser_id());
        }
        if (CommonUtils.stringIsNotBlack(entity.getName())) {
            searchInfo.like("name", entity.getName());
        }

        searchInfo.orderBy(true, false, "start_time");
        IPage<Plan> pageInfo = new Page<>(page, limit);
        IPage<Plan> selectPage = PlanMapper.selectPage(pageInfo, searchInfo);
        return selectPage;
    }

    @Override
    public Plan selectByPrimaryKey(String id) {
        Plan Plan = PlanMapper.selectById(id);
        return Plan;
    }

    @Override
    public boolean insert(Plan entity) {
        entity.setUser_id(authUserUtils.getLoginUserId());
        Integer result = PlanMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Plan entity) {
        Integer result = PlanMapper.updateById(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        Integer result = 0;
        for (String id : ids) {
            result = PlanMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Plan> selectList(Plan entity) {
        QueryWrapper<Plan> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getUser_id())) {
            wrapper.like("user_id", entity.getUser_id());
        }
        wrapper.orderBy(true, false, "Plan_time");
        List<Plan> PlanList = PlanMapper.selectList(wrapper);
        return PlanList;
    }
}