package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Notice;
import com.it.mapper.NoticeMapper;
import com.it.service.NoticeService;
import com.it.util.CommonUtils;
import com.it.util.DateUtil;
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
public class NoticeServiceImpl implements NoticeService {

    @Resource
    private NoticeMapper NoticeMapper;


    @Override
    public IPage<Notice> selectPage(Notice entity, int page, int limit) {
        QueryWrapper<Notice> searchInfo = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getTitle())) {
            searchInfo.like("title", entity.getTitle());
        }
        if (CommonUtils.stringIsNotBlack(entity.getCreate_time())) {
            searchInfo.between("create_time", entity.getCreate_time().split(" - ")[0], entity.getCreate_time().split(" - ")[1]);
        }
        searchInfo.orderBy(true, false, "create_time");
        IPage<Notice> pageInfo = new Page<>(page, limit);
        IPage<Notice> selectPage = NoticeMapper.selectPage(pageInfo, searchInfo);
        return selectPage;
    }

    @Override
    public Notice selectByPrimaryKey(String id) {
        Notice Notice = NoticeMapper.selectById(id);
        Notice.setContent(EmojiFilter.emojiRecovery2(Notice.getContent()));
        return Notice;
    }

    @Override
    public boolean insert(Notice entity) {
        entity.setCreate_time(DateUtil.getNowDateSS());
        entity.setContent(EmojiFilter.emojiConvert1(entity.getContent()));
        Integer result = NoticeMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Notice entity) {
        entity.setContent(EmojiFilter.emojiConvert1(entity.getContent()));
        Integer result = NoticeMapper.updateById(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        Integer result = 0;
        for (String id : ids) {
            result = NoticeMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Notice> selectList(Notice entity) {
        QueryWrapper<Notice> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getTitle())) {
            wrapper.like("title", entity.getTitle());
        }
        wrapper.orderBy(true, false, "create_time");
        List<Notice> NoticeList = NoticeMapper.selectList(wrapper);
        return NoticeList;
    }
}