package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Reply;
import com.it.entity.User;
import com.it.mapper.ReplyMapper;
import com.it.mapper.UserMapper;
import com.it.service.ReplyService;
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
public class ReplyServiceImpl implements ReplyService {

    @Resource
    private ReplyMapper ReplyMapper;
    @Resource
    private AuthUserUtils authUserUtils;
    @Resource
    private UserMapper userMapper;

    @Override
    public IPage<Reply> selectPage(Reply entity, int page, int limit) {
        QueryWrapper<Reply> searchInfo = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getItem_id())) {
            searchInfo.like("item_id", entity.getItem_id());
        }
        if (CommonUtils.stringIsNotBlack(entity.getUser_id())) {
            searchInfo.like("user_id", entity.getUser_id());
        }
        if (CommonUtils.stringIsNotBlack(entity.getCreate_time())) {
            searchInfo.between("create_time", entity.getCreate_time().split(" - ")[0], entity.getCreate_time().split(" - ")[1]);
        }
        searchInfo.orderBy(true, false, "create_time");
        IPage<Reply> pageInfo = new Page<>(page, limit);
        IPage<Reply> selectPage = ReplyMapper.selectPage(pageInfo, searchInfo);
        if (!selectPage.getRecords().isEmpty()) {
            for (Reply record : selectPage.getRecords()) {
                User user = userMapper.selectById(record.getUser_id());
                record.setUser(user == null ? new User() : user);
                record.setContent(EmojiFilter.emojiRecovery2(record.getContent()));

                User send = userMapper.selectById(record.getSend_id());
                record.setSend_name(send == null ? "*注销用户*" : send.getNick_name());
            }
        }
        return selectPage;
    }

    @Override
    public Reply selectByPrimaryKey(String id) {
        Reply Reply = ReplyMapper.selectById(id);
        return Reply;
    }

    @Override
    public boolean insert(Reply entity) {
        entity.setCreate_time(DateUtil.getNowDateSS());
        entity.setContent(EmojiFilter.emojiConvert1(entity.getContent()));
        entity.setUser_id(authUserUtils.getLoginUserId());
        Integer result = ReplyMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Reply entity) {
        entity.setContent(EmojiFilter.emojiConvert1(entity.getContent()));
        Integer result = ReplyMapper.updateById(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        Integer result = 0;
        for (String id : ids) {
            result = ReplyMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Reply> selectList(Reply entity) {
        QueryWrapper<Reply> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getItem_id())) {
            wrapper.like("item_id", entity.getItem_id());
        }
        wrapper.orderBy(true, false, "create_time");
        List<Reply> ReplyList = ReplyMapper.selectList(wrapper);
        return ReplyList;
    }

    @Override
    public List<Reply> selectList(String item_id) {
        QueryWrapper<Reply> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(item_id)) {
            wrapper.like("item_id", item_id);
        }
        wrapper.orderBy(true, false, "create_time");
        List<Reply> ReplyList = ReplyMapper.selectList(wrapper);
        if (!ReplyList.isEmpty()) {
            for (Reply record : ReplyList) {
                User user = userMapper.selectById(record.getUser_id());
                record.setUser(user == null ? new User() : user);
                record.setContent(EmojiFilter.emojiRecovery2(record.getContent()));
                User send = userMapper.selectById(record.getSend_id());
                record.setSend_name(send == null ? "*注销用户*" : send.getNick_name());
            }
        }
        return ReplyList;

    }
}