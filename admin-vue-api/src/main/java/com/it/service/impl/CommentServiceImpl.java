package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Comment;
import com.it.entity.User;
import com.it.mapper.CommentMapper;
import com.it.mapper.UserMapper;
import com.it.service.CommentService;
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
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper CommentMapper;
    @Resource
    private AuthUserUtils authUserUtils;
    @Resource
    private UserMapper userMapper;

    @Override
    public IPage<Comment> selectPage(Comment entity, int page, int limit) {
        QueryWrapper<Comment> searchInfo = new QueryWrapper<>();
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
        IPage<Comment> pageInfo = new Page<>(page, limit);
        IPage<Comment> selectPage = CommentMapper.selectPage(pageInfo, searchInfo);
        if (!selectPage.getRecords().isEmpty()) {
            for (Comment record : selectPage.getRecords()) {
                User user = userMapper.selectById(record.getUser_id());
                record.setUser(user == null ? new User() : user);
                record.setContent(EmojiFilter.emojiRecovery2(record.getContent()));
            }
        }
        return selectPage;
    }

    @Override
    public Comment selectByPrimaryKey(String id) {
        Comment Comment = CommentMapper.selectById(id);
        return Comment;
    }

    @Override
    public boolean insert(Comment entity) {
        entity.setCreate_time(DateUtil.getNowDateSS());
        entity.setContent(EmojiFilter.emojiConvert1(entity.getContent()));
        entity.setUser_id(authUserUtils.getLoginUserId());
        Integer result = CommentMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Comment entity) {
        entity.setContent(EmojiFilter.emojiConvert1(entity.getContent()));
        Integer result = CommentMapper.updateById(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        Integer result = 0;
        for (String id : ids) {
            result = CommentMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Comment> selectList(Comment entity) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getItem_id())) {
            wrapper.like("item_id", entity.getItem_id());
        }
        wrapper.orderBy(true, false, "create_time");
        List<Comment> CommentList = CommentMapper.selectList(wrapper);
        return CommentList;
    }

    @Override
    public List<Comment> selectList(String item_id) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(item_id)) {
            wrapper.like("item_id", item_id);
        }
        wrapper.orderBy(true, false, "create_time");
        List<Comment> CommentList = CommentMapper.selectList(wrapper);
        return CommentList;
    }
}