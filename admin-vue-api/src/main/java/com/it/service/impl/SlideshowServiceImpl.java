package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Slideshow;
import com.it.mapper.SlideshowMapper;
import com.it.service.SlideshowService;
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
public class SlideshowServiceImpl implements SlideshowService {

    @Resource
    private SlideshowMapper SlideshowMapper;


    @Override
    public IPage<Slideshow> selectPage(Slideshow entity, int page, int limit) {
        QueryWrapper<Slideshow> searchInfo = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getItem_id())) {
            searchInfo.like("item_id", entity.getItem_id());
        }
        if (CommonUtils.stringIsNotBlack(entity.getCreate_time())) {
            searchInfo.between("create_time", entity.getCreate_time().split(" - ")[0], entity.getCreate_time().split(" - ")[1]);
        }
        searchInfo.orderBy(true, false, "create_time");
        IPage<Slideshow> pageInfo = new Page<>(page, limit);
        IPage<Slideshow> selectPage = SlideshowMapper.selectPage(pageInfo, searchInfo);
        return selectPage;
    }

    @Override
    public Slideshow selectByPrimaryKey(String id) {
        Slideshow Slideshow = SlideshowMapper.selectById(id);
        return Slideshow;
    }

    @Override
    public boolean insert(Slideshow entity) {
        entity.setCreate_time(DateUtil.getNowDateSS());
        Integer result = SlideshowMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Slideshow entity) {
        Integer result = SlideshowMapper.updateById(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        Integer result = 0;
        for (String id : ids) {
            result = SlideshowMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Slideshow> selectList(Slideshow entity) {
        QueryWrapper<Slideshow> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getItem_id())) {
            wrapper.like("item_id", entity.getItem_id());
        }
        wrapper.orderBy(true, false, "create_time");
        List<Slideshow> SlideshowList = SlideshowMapper.selectList(wrapper);
        return SlideshowList;
    }
}