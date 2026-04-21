package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Log;
import com.it.mapper.LogMapper;
import com.it.service.LogService;
import com.it.util.CommonUtils;
import com.it.util.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 〈日志service实现类〉<br>
 *
 * @author
 */
@Service
public class LogServiceImpl implements LogService {
    @Resource
    private LogMapper logMapper;


    @Override
    public IPage<Log> selectPage(Log entity, int page, int limit) {
        QueryWrapper<Log> searchInfo = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getName())) {
            searchInfo.like("name", entity.getName());
        }
        if (CommonUtils.stringIsNotBlack(entity.getOperation_result())) {
            searchInfo.like("operation_result", entity.getOperation_result());
        }
        if (CommonUtils.stringIsNotBlack(entity.getOperation())) {
            searchInfo.like("operation", entity.getOperation());
        }
        if (CommonUtils.stringIsNotBlack(entity.getStartTime())) {
            searchInfo.between("create_time", entity.getStartTime(), entity.getEndTime());
        }

        searchInfo.orderBy(true, false, "create_time");
        IPage<Log> pageInfo = new Page<>(page, limit);
        IPage<Log> selectPage = logMapper.selectPage(pageInfo, searchInfo);
        return selectPage;
    }

    @Override
    public Log selectByPrimaryKey(String id) {
        return logMapper.selectById(id);
    }

    @Override
    public boolean insert(Log entity) {
        entity.setCreate_time(DateUtil.getNowDateSS());
        Integer result = logMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }


    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        Integer result = 0;
        for (String id : ids) {
            result = logMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }
}