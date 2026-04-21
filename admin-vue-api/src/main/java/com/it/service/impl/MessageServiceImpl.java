package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Message;
import com.it.mapper.MessageMapper;
import com.it.service.MessageService;
import com.it.util.AuthUserUtils;
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
public class MessageServiceImpl implements MessageService {

    @Resource
    private MessageMapper MessageMapper;
    @Resource
    private AuthUserUtils authUserUtils;

    @Override
    public IPage<Message> selectPage(Message entity, int page, int limit) {
        QueryWrapper<Message> searchInfo = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getUser_id())) {
            searchInfo.eq("user_id", entity.getUser_id());
        }
        if (CommonUtils.stringIsNotBlack(entity.getCreate_time())) {
            searchInfo.between("create_time", entity.getCreate_time().split(" - ")[0], entity.getCreate_time().split(" - ")[1]);
        }
        searchInfo.orderBy(true, false, "create_time");
        IPage<Message> pageInfo = new Page<>(page, limit);
        IPage<Message> selectPage = MessageMapper.selectPage(pageInfo, searchInfo);
        if (!selectPage.getRecords().isEmpty()) {
            for (Message record : selectPage.getRecords()) {
                record.setContent(EmojiFilter.emojiRecovery2(record.getContent()));
            }
        }
        return selectPage;
    }

    @Override
    public Message selectByPrimaryKey(String id) {
        Message Message = MessageMapper.selectById(id);
        return Message;
    }

    @Override
    public boolean insert(Message entity) {
        entity.setCreate_time(DateUtil.getNowDateSS());
        entity.setContent(EmojiFilter.emojiConvert1(entity.getContent()));
        entity.setState("0");
        Integer result = MessageMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Message entity) {
        entity.setContent(EmojiFilter.emojiConvert1(entity.getContent()));
        Integer result = MessageMapper.updateById(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        Integer result = 0;
        for (String id : ids) {
            result = MessageMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Message> selectList(Message entity) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getUser_id())) {
            wrapper.like("user_id", entity.getUser_id());
        }
        if (CommonUtils.stringIsNotBlack(entity.getType())) {
            wrapper.like("type", entity.getType());
        }
        if (CommonUtils.stringIsNotBlack(entity.getState())) {
            wrapper.like("state", entity.getState());
        }
        wrapper.orderBy(true, false, "create_time");
        List<Message> MessageList = MessageMapper.selectList(wrapper);
        return MessageList;
    }
}